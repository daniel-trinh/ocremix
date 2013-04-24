package com.sixnothings.maestro

import akka.actor.{ Actor, ActorSystem, Props }
import akka.util.duration._
import dispatch.Defaults._
import com.sixnothings.twitter.api.{Tweetable, Auth, ApiClient}
import com.sixnothings.config.{TwitterHandle, TwitterSettings}
import akka.event.Logging
import com.sixnothings.ocremix.RSS
import com.codahale.jerkson.Json._
import dispatch._
import scala.Left
import scala.Right
import com.sixnothings.config.TwitterHandle

abstract class LoggedActor extends Actor {
  val log = Logging(context.system, this)
}

/**
 * Used to enable actor logging in actor classes that inherit from this.
 */
abstract class LoggedDirectMessageActor extends LoggedActor {

  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(
      """
        |Error message: %s
        |Stack Trace: %s
      """.stripMargin.format(reason.getMessage, reason.getStackTraceString))
    context.actorFor("../directMessager") ! "Unhandled exception:" + reason.getMessage
    context.children foreach context.stop
    postStop()
  }
}

/**
 * Dummy actor used for testing to make sure Akka is set up properly
 */
class HelloActor extends LoggedDirectMessageActor {
  def receive = {
    case "hello" => log.info("hello back at you")
    case "world" => context.actorFor("../world") ! "hello"
    case msg     => log.info(msg.toString)
  }
}

/**
 * Dummy actor used for testing to make sure Akka is set up properly
 */
class WorldActor extends LoggedDirectMessageActor {
  def receive = {
    case "hello" => log.info("hello world!")
    case msg => log.info("wtf?" + msg.toString)
  }
}

/**
 * Used to update the TwitterSettings.configuration Agent.
 */
class UpdateTwitterConfigActor(client: ApiClient) extends LoggedDirectMessageActor {
  def receive = {
    case "doit" => client.helpConfiguration.onSuccess {
      // onSuccess handles both success and failure, because
      // client methods are designed to never throw an exception (by using
      // Either).
      case response => response match {
        case Right(config) => {
          TwitterSettings.configuration send config
          log.info("Config updated: %s".format(config.toString))
        }
        case Left(error) => {
          context.actorFor("../directMessager") ! (error)
          log.error(error)
        }
      }
    }
    case unexpectedPattern => log.error(unexpectedPattern.toString)
  }
}

/**
 * Used primarily for sending error alerts to a panic twitter handle,
 * in case something goes terribly wrong.
 */
class SendDirectMessageActor(
  client: ApiClient,
  handle: TwitterHandle = TwitterSettings.panicHandle) extends LoggedDirectMessageActor {
  // onSuccess handles both success and failure, because
  // client methods are designed to never throw an exception (by using
  // Either).
  def receive = {
    case (message: String) => {
      log.error(message)
      client.directMessage(handle, message).onSuccess {
        case Right(response) => log.info(response)
        case Left(error) => log.error(error)
      }
    }
  }
}

/**
 * Used to Tweetable messages to Twitter.
 */
class TweeterActor(client: ApiClient) extends LoggedDirectMessageActor {
  def receive = {
    case tweet @ Tweetable(message) => {
      // onSuccess handles both success and failure, because
      // client methods are designed to never throw an exception (by using
      // Either).
      client.statusesUpdate(tweet)() match {
        case Right(response) => log.info(response)
        case Left(error) =>
          context.actorFor("../directMessager") ! (error)
          log.error(error)
      }
    }
  }
}

/**
 * Parses OCRemix RSS XML into a tweetable format, and then sends a message
 * to TweeterActor for tweeting
 */
class OCRemixRssPollerActor(client: ApiClient) extends LoggedDirectMessageActor {
  def receive = {
    case "doit" => {
      RSS.fetch.onSuccess {
        case Right(rssResponse) => {
          val remixes = RSS.extractRemixes(rssResponse)
          // TODO: extract this block of code into a function somewhere else
          val idRegex = """(\d+):.+""".r

          client.userTimeline(
            userId = TwitterSettings.ocremixHandle.id,
            screenName = TwitterSettings.ocremixHandle.screenName,
            count = 10
          ).onSuccess {
            case Right(jsonResponse) => {
              val latestRemixTweets = parse[List[Map[String, Any]]](jsonResponse)

              // extract latest tweet ID from our twitter stream. Assumes head of list is latest tweet.
              val latestRemixTweetId = latestRemixTweets match {
                // TODO: replace this with a number besides 0?
                case Nil => "0"
                case tweet :: _ => tweet("text") match {
                  case idRegex(id) => id
                }
              }

              // flip List[Future[RemixEntry]] to Future[List[RemixEntry]] for simpler processing
              val sequencedRemixes = Future.sequence(remixes)

              // we only want to tweet the latest remixes, in order they are posted
              sequencedRemixes.map { remixes =>
                remixes.view.filter { remix =>
                  remix.songId > latestRemixTweetId.toInt
                  // remixes are ordered from newest to oldest, but we want to tweet oldest to newest,
                  // so reverse them
                }.reverse.foreach { untweetedRemix =>
                  context.actorFor("../tweeter") ! untweetedRemix.toTweetable
                  log.info(untweetedRemix.toTweetable.toString)
                }
              }
            }
          }
        }
      }
    }
  }
}

class Supervisor extends LoggedDirectMessageActor {

  val helloActor     = context.actorOf(Props[HelloActor], name = "helloActor")
  val worldActor     = context.actorOf(Props[WorldActor], name = "world")
  val rssPoller      = context.actorOf(Props(new OCRemixRssPollerActor(MySystem.client)), name = "rssPoller")
  val configUpdater  = context.actorOf(Props(new UpdateTwitterConfigActor(MySystem.client)), name = "configUpdater")
  val directMessager = context.actorOf(Props(new SendDirectMessageActor(MySystem.client)), name = "directMessager")
  val tweeter        = context.actorOf(Props(new TweeterActor(MySystem.client)), name = "tweeter")

  val helloWorld = MySystem().scheduler.schedule(
    initialDelay = 1 milliseconds,
    frequency    = 1 hour,
    receiver     = helloActor,
    message      = "world"
  )

  val twitterConfigUpdaterSchedule = MySystem().scheduler.schedule(
    initialDelay = 0 seconds,
    frequency    = 1 day,
    receiver     = configUpdater,
    message      = "doit"
  )

  val rssPollerSchedule = MySystem().scheduler.schedule(
    initialDelay = 0 seconds,
    frequency    = 30 minutes,
    receiver     = rssPoller,
    message      = "doit"
  )

  def receive = {
    case p: Props ⇒ sender ! context.actorOf(p)
  }
}

case object MySystem {
  val system = ActorSystem("Scheduler")
  def apply() = system
  val client = new ApiClient(new Auth(""))
}

/**
 * The starting point of the application. Sets up four actors for parsing OCRemix's RSS:
 * 1) [[com.sixnothings.maestro.OCRemixRssPollerActor]]
 *    handles polling and parsing of the ocremix RSS URL. Sends new songs to [[com.sixnothings.maestro.TweeterActor]]
 * 2) [[com.sixnothings.maestro.UpdateTwitterConfigActor]]
 *    periodically updates the twitter t.co url shortening config, in case it has changed
 * 3) [[com.sixnothings.maestro.SendDirectMessageActor]]
 *    sends directMessages to an alt tweet user in case an error occurs
 * 4) [[com.sixnothings.maestro.TweeterActor]]
 *    posts new songs to Twitter
 */
object Main extends App {
  val system = MySystem()

  val supervisor = system.actorOf(Props[Supervisor], "supervisor")

  import akka.actor.{ Actor, DeadLetter, Props }

  // Register a listener to send deadletter messages
  val listener = system.actorOf(Props(new Actor {
    def receive = {
      case d: DeadLetter => system.actorFor("../directMessager") ! "DeadLetter: " + d.toString()
    }
  }))

  system.eventStream.subscribe(listener, classOf[DeadLetter])
}