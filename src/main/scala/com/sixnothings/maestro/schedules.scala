package com.sixnothings.maestro

import akka.actor.{ Actor, ActorSystem, Props }
import akka.util.duration._
import com.sixnothings.twitter.api.{ApiClient, Tweet}

/**
 * Dummy actor used for testing to make sure Akka is set up properly
 */
class HelloActor extends Actor {
  def receive = {
    case "hello" => println("hello back at you")
    case _       => System.out.println("huh?")
  }
}

/**
 * Used to update the TwitterSettings.configuration Agent.
 */
class UpdateTwitterConfigActor extends Actor {
  def receive = {
    case _ => "yep"
  }
}

/**
 * Used primarily for sending error alerts to a panic twitter handle,
 * in case something goes terribly wrong.
 */
class SendDirectMessageActor extends Actor {
  def receive = {
    case _ => "yep"
  }
}

/**
 * Used to Tweet messages to Twitter.
 */
class TweeterActor(client: ApiClient) extends Actor {
  def receive = {
    case message: String =>
    case tweet @ Tweet(message) => {
      client.statusesUpdate(tweet).onFailure()

    }
    case _ =>
  }
}

/**
 * Parses OCRemix RSS XML into a tweetable format, and then sends a message
 * to TweeterActor for tweeting.
 */
class OCRemixRSSParserActor extends Actor {
  def receive = {
    case _ => "yep"
  }
}

class OCRemixRssPollerActor extends Actor {
  def receive = {
    case "doit" => {

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

object Main extends App {
  val message = "Hello"
  val system = MySystem()

  val configUpdater = system.actorOf(Props[UpdateTwitterConfigActor], name = "configUpdater")
  val rssPollerActor = system.actorOf(Props[OCRemixRssPollerActor], name = "rssPoller")


  val twitterConfigUpdateSchedule = system.scheduler.schedule(
    initialDelay = 0 seconds,
    frequency    = 1 day,
    receiver     = configUpdater,
    message      = "doit"
  )

  val rssPollerSchedule = system.scheduler.schedule(
    initialDelay = 0 seconds,
    frequency    = 30 minutes,
    receiver     = rssPollerActor,
    message      = "doit"
  )
}