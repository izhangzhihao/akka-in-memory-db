package com.github.izhangzhihao.client

import akka.actor.{ActorSystem, Props}

object run extends App {
  val system = ActorSystem("akka-in-memory-db")
  val server = system.actorOf(Props[Client], name = "akka-in-memory-db-client")
}
