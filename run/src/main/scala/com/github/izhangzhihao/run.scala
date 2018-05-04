package com.github.izhangzhihao

import akka.actor.{ActorRef, ActorSystem, Props}
import com.github.izhangzhihao.client.{Client, Flush}
import com.github.izhangzhihao.server.InMemoryDb
import com.github.izhangzhihao.commons._

object run extends App {
  val system = ActorSystem("akka-in-memory-db")
  val server = system.actorOf(Props[InMemoryDb], name = "akka-in-memory-db-server")

  implicit val clientRef: ActorRef = system.actorOf(Props(classOf[Client], "akka.tcp://akka-in-memory-db@127.0.0.1:2552/user/akka-in-memory-db-server"), "client")

  clientRef ! Set("key", "value", implicitly)
  Thread.sleep(1000)
  clientRef ! Flush


  clientRef ! Get("key", implicitly)
  clientRef ! Get("not exist", implicitly)
  clientRef ! Flush


  //  server ! Ping()
  //
  //  server ! List(
  //    Get("not exist", clientRef),
  //    Set("key", "value", clientRef)
  //  )


}