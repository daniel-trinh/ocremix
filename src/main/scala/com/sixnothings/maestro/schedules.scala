package com.sixnothings.maestro

import akka.actor.{ Actor, ActorSystem, Props }
import scala.concurrent.duration._

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
  def receive = throw new NotImplementedError 
}

/**
 * Used primarily for sending error alerts to a panic twitter handle,
 * in case something goes terribly wrong.
 */
class SendDirectMessageActor extends Actor {
  def receive = throw new NotImplementedError 
}

/**
 * Used to Tweet messages to Twitter.
 */
class TweeterActor extends Actor {
  def receive = throw new NotImplementedError 
}

/**
 * Parses OCRemix RSS XML into a tweetable format, and then sends a message
 * to TweeterActor for tweeting.
 */
class OCRemixRSSParserActor extends Actor {
  def receive = throw new NotImplementedError 
}

case object MySystem {
  val system = ActorSystem("Scheduler")
  def apply() = system
}

object Main extends App {
  val message = "Hello"
  val system = MySystem()
  import system.dispatcher
  val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")

  val cancellable = system.scheduler.schedule(
    initialDelay = 1 milliseconds,
    interval     = 1 second,
    receiver     = helloActor,
    message      = "test"
  )
}