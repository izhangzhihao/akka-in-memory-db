package com.github.izhangzhihao.server

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.TestActorRef
import org.scalatest.{FunSpecLike, Matchers}
import com.github.izhangzhihao.commons._

class InMemoryDbSpec extends FunSpecLike with Matchers {

  implicit val system = ActorSystem()
  implicit val sender = ActorRef.noSender

  describe("in memory db") {
    describe("should receive request") {
      it("it should receive request") {
        val actorRef = TestActorRef(new InMemoryDb)
        actorRef ! Set("key", "value", implicitly)
        val db = actorRef.underlyingActor
        db.map.get("key") shouldEqual Some("value")
      }
    }
  }
}
