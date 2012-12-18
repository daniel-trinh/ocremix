package com.sixnothings.maestro

import akka.actor.{ Actor, ActorSystem, Props }
import scala.concurrent.duration._

class HelloActor extends Actor {
  def receive = {
    case "hello" => println("hello back at you")
    case _       => System.out.println("huh?")
  }
}

case object MySystem {
  val system = ActorSystem("ScheduleSystem")
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