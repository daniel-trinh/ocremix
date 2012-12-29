package com.sixnothings.maestro

import akka.actor.{ Actor, ActorSystem, Props }
import akka.util.duration._
import com.sixnothings.twitter.api.{Tweet, Auth, ApiClient}
import com.sixnothings.config.{TwitterHandle, TwitterSettings}
import akka.event.Logging


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
    case _       => log.error("huh?")
  }
}

/**
 * Used to update the TwitterSettings.configuration Agent.
 */
class UpdateTwitterConfigActor extends LoggedActor {
  def receive = {
    case apiClient: ApiClient => apiClient.helpConfiguration.onSuccess {
      // onSuccess handles both success and failure, because
      // apiClient methods are designed to never throw an exception (by using
      // Either).
      case response => response match {
        case Right(config) => {
          TwitterSettings.configuration send config
          log.info("Config updated: %s".format(config.toString))
        }
        case Left(error) => {
          Actors.directMessageActor ! (apiClient, TwitterSettings.panicHandle, error)
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
class SendDirectMessageActor extends LoggedActor {
  // onSuccess handles both success and failure, because
  // apiClient methods are designed to never throw an exception (by using
  // Either).
  def receive = {
    case (apiClient: ApiClient, handle: TwitterHandle, message: String) => {
      apiClient.directMessage(handle, message).onSuccess {
        case Right(response) => log.info(response)
        case Left(error) => log.error(error)
      }
    }
  }
}

/**
 * Used to Tweet messages to Twitter.
 */
class TweeterActor extends LoggedActor {
  def receive = {
    case (apiClient: ApiClient, tweet @ Tweet(message)) => {
      // onSuccess handles both success and failure, because
      // apiClient methods are designed to never throw an exception (by using
      // Either).
      apiClient.statusesUpdate(tweet).onSuccess {
        case Right(response) => log.info(response)
        case Left(error) =>
          Actors.directMessageActor ! (apiClient, TwitterSettings.panicHandle, error)

      }
    }
  }
}

/**
 * Parses OCRemix RSS XML into a tweetable format, and then sends a message
 * to TweeterActor for tweeting.
 */
class OCRemixRSSParserActor extends LoggedActor {
  // onSuccess handles both success and failure, because
  // apiClient methods are designed to never throw an exception (by using
  // Either).
  def receive = {
    case _ => "yep"
  }
}

case object MySystem {
  val system = ActorSystem("Scheduler")
  def apply() = system
}

case object Actors {
  val client = new ApiClient(new Auth(""))
  val system = MySystem()
  val helloActor = system.actorOf(Props[HelloActor], name = "helloActor")
  val configActor = system.actorOf(Props[UpdateTwitterConfigActor], name = "configActor")
  val directMessageActor = system.actorOf(Props[SendDirectMessageActor], name = "directMessageActor")
}

object Main extends App {
  val cancellable = Actors.system.scheduler.schedule(
    initialDelay = 1 milliseconds,
    frequency    = 1 hour,
    receiver     = Actors.helloActor,
    message      = "test"
  )
//  val cancellable2 = Actors.system.scheduler.schedule(
//    initialDelay = 1 milliseconds,
//    frequency    = 1 hour,
//    receiver     = Actors.configActor,
//    message      = Actors.client
//  )
}