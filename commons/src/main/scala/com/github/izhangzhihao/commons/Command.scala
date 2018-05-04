package com.github.izhangzhihao.commons

import akka.actor.ActorRef

sealed trait Command

case class Set(key: String, value: String, sender: ActorRef = ActorRef.noSender) extends Command

case class Get(key: String, sender: ActorRef = ActorRef.noSender) extends Command

case class Response(value: String)

case object Ping

case object Pong

class KeyNotFoundException(key: String) extends RuntimeException(key)

class UnknownCommandException(message: String) extends RuntimeException(message)
