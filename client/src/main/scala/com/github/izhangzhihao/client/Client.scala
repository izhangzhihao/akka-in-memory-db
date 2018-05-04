package com.github.izhangzhihao.client

import akka.actor.{FSM, Status}
import akka.event.Logging
import com.github.izhangzhihao.client.StateContainerTypes.CommandQueue
import com.github.izhangzhihao.commons._


sealed trait State

case object Disconnected extends State

case object Connected extends State

case object ConnectedAndPending extends State

case object Flush

object StateContainerTypes {
  type CommandQueue = List[Command]
}

class Client(address: String) extends FSM[State, CommandQueue] {
  private val remoteDb = context.system.actorSelection(address)

  private val logger = Logging(context.system, this)

  startWith(Disconnected, List.empty[Command])

  when(Disconnected) {
    case Event(Pong, container: CommandQueue) => //If we get back a pong from db, change state
      if (container.headOption.isEmpty) {
        logger.info("[client] connect to server success, status change to Connected.")
        goto(Connected)
      } else {
        logger.warning("[client] server not connected, but receive new command, status change to ConnectedAndPending.")
        goto(ConnectedAndPending)
      }

    case Event(x: Command, container: CommandQueue) =>
      logger.info("[client] now going to ping server.")
      remoteDb ! Ping //Ping remote db to see if we're connected if not yet marked online.
      stay using (container :+ x) //Stash the msg
    case x =>
      logger.warning(s"[client] Unknown command $x")
      stay()
  }

  when(Connected) {
    case Event(x: Command, container: CommandQueue) =>
      logger.info("[client] new command received, will been processed soon.")
      goto(ConnectedAndPending) using (container :+ x)
    case Event(s: Status.Status, stateData: Any) =>
      logger.info(s"[client] receive new response from server, status: $s , data: $stateData")
      stay()
    case Event(res: Response, _: CommandQueue) =>
      logger.info(s"[client] receive response from server: $res")
      stay()
  }

  when(ConnectedAndPending) {
    case Event(Flush, container) =>
      logger.info("[client] now flush container.")
      remoteDb ! container
      goto(Connected) using Nil
    case Event(x: Command, container: CommandQueue) =>
      logger.info("[client] new command received, will been processed soon.")
      stay using (container :+ x)
  }

  initialize()
}
