package com.sixnothings.maestro

import akka.actor.{ Actor, ActorSystem, Props }
import akka.util.duration._
import com.sixnothings.twitter.api.{Tweetable, Auth, ApiClient}
import com.sixnothings.config.{TwitterHandle, TwitterSettings}
import akka.event.Logging
import com.sixnothings.ocremix.RSS
import com.codahale.jerkson.Json._
import com.sixnothings.twitter.json.{Tweet}

/**
 * Used to enable actor logging in actor classes that inherit from this.
 */
abstract class LoggedActor extends Actor {
  val log = Logging(context.system, this)
}

/**
 * Dummy actor used for testing to make sure Akka is set up properly
 */
class HelloActor extends LoggedActor {
  def receive = {
    case "hello" => log.info("hello back at you")
    case msg     => log.info(msg.toString)
  }
}

/**
 * Used to update the TwitterSettings.configuration Agent.
 */
class UpdateTwitterConfigActor(client: ApiClient) extends LoggedActor {
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
          Actors.directMessager ! (error)
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
  handle: TwitterHandle = TwitterSettings.panicHandle) extends LoggedActor {
  // onSuccess handles both success and failure, because
  // client methods are designed to never throw an exception (by using
  // Either).
  def receive = {
    case (message: String) => {
      log.error(message)
//      client.directMessage(handle, message).onSuccess {
//        case Right(response) => log.info(response)
//        case Left(error) => log.error(error)
//      }
    }
  }
}

/**
 * Used to Tweetable messages to Twitter.
 */
class TweeterActor(client: ApiClient) extends LoggedActor {
  def receive = {
    case tweet @ Tweetable(message) => {
      // onSuccess handles both success and failure, because
      // client methods are designed to never throw an exception (by using
      // Either).
      client.statusesUpdate(tweet).onSuccess {
        case Right(response) => log.info(response)
        case Left(error) =>
          Actors.directMessager ! (error)
          log.error(error)
      }
    }
  }
}

/**
 * Parses OCRemix RSS XML into a tweetable format, and then sends a message
 * to TweeterActor for tweeting
 */
class OCRemixRssPollerActor(client: ApiClient) extends LoggedActor {
  def receive = {
    case "doit" => {
      RSS.fetch.onSuccess {
        case Right(rssResponse) => {
          val remixes = RSS.extractRemixes(rssResponse)
          // TODO: extract this block of code into a function somewhere else
          val idRegex = """^(\d+):""".r
          client.userTimeline(
            userId = TwitterSettings.ocremixHandle.id,
            screenName = TwitterSettings.ocremixHandle.screenName,
            count = 10
          ).onSuccess {
            case Right(jsonResponse) => {
              val latestRemixTweet = parse[List[Tweet]](jsonResponse).head
              val idRegex(latestRemixTweetId) = latestRemixTweet
              remixes.takeWhile { remix =>
                remix.songId >= latestRemixTweetId.toInt
              }.foreach { untweetedRemix =>
                log.info(untweetedRemix.toTweetable.toString)
                //Actors.tweeter ! untweetedRemix.toTweetable
              }
            }
            case Left(error) => {
              Actors.directMessager ! error
            }
          }
        }
        case Left(error) =>
          Actors.directMessager ! (error)
      }
    }
    case _ => {
      "???"
    }
  }
}

case object MySystem {
  val system = ActorSystem("Scheduler")
  def apply() = system
}

case object Actors {
  val client = new ApiClient(new Auth(""))
  val system = MySystem()
  val rssPoller      = system.actorOf(Props(new OCRemixRssPollerActor(client)), name = "rssPoller")
  val helloActor     = system.actorOf(Props[HelloActor], name = "helloActor")
  val configUpdater  = system.actorOf(Props(new UpdateTwitterConfigActor(client)), name = "configActor")
  val directMessager = system.actorOf(Props(new SendDirectMessageActor(client)), name = "directMessageActor")
  val tweeter        = system.actorOf(Props(new TweeterActor(client)), name = "tweeter")
}

object Main extends App {
  val helloWorld = Actors.system.scheduler.schedule(
    initialDelay = 1 milliseconds,
    frequency    = 1 hour,
    receiver     = Actors.helloActor,
    message      = "test")

  val twitterConfigUpdaterSchedule = Actors.system.scheduler.schedule(
    initialDelay = 0 seconds,
    frequency    = 1 day,
    receiver     = Actors.configUpdater,
    message      = "doit"
  )

  val rssPollerSchedule = Actors.system.scheduler.schedule(
    initialDelay = 0 seconds,
    frequency    = 30 minutes,
    receiver     = Actors.rssPoller,
    message      = "doit"
  )
}