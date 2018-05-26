package com.github.izhangzhihao.server

import akka.actor.{Actor, Status}
import akka.event.Logging
import com.github.izhangzhihao.commons._

import scala.collection.concurrent.TrieMap

class InMemoryDb extends Actor {
  val map = new TrieMap[String, Object]
  val logger = Logging(context.system, this)

  override def receive: PartialFunction[Any, Unit] = {
    case Ping =>
      sender() ! Pong
    case x: List[_] =>
      x.foreach {
        case cmd: Set =>
          handleSet(cmd)
        case cmd: Get =>
          handleGet(cmd)
      }
    case cmd: Set =>
      handleSet(cmd)
    case cmd: Get =>
      handleGet(cmd)
    case cmd =>
      logger.warning(s"[server] unknown message: $cmd")
      sender() ! Status.Failure(new UnknownCommandException(cmd.toString))
  }

  def handleSet(cmd: Set): Unit = {
    val (key, value, senderRef) = (cmd.key, cmd.value, cmd.sender)
    logger.info(s"[server] received Set command - key: $key value: $value")
    map.put(key, value)
    senderRef ! Status.Success
  }

  def handleGet(cmd: Get): Unit = {
    val (key, senderRef) = (cmd.key, cmd.sender)
    logger.info(s"[server] received Get command - key: $key")
    map.get(key) match {
      case Some(x) => senderRef ! Response(x.toString)
      case None => senderRef ! Status.Failure(new KeyNotFoundException(key))
    }
  }
}