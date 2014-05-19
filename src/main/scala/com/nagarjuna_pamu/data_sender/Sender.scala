package com.nagarjuna_pamu.data_sender

import java.net.InetSocketAddress
import akka.io.IO
import akka.io.Udp
import akka.actor.Actor
import akka.actor.ActorRef
import akka.util.ByteString
import akka.actor.PoisonPill
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import com.nagarjuna_pamu._

case object Kill
case object Start
case object StartTimer
case object CancelTimer
case object Timeout
case object Retry
class Sender(remote: InetSocketAddress) extends Actor {
  import context.system
  var counter = 0
  var tries = 0
  var snum = 0
  
  IO(Udp) ! Udp.SimpleSender
  
  def receive = {
    case Udp.SimpleSenderReady => context.become(ready(sender))
  }
  
  def ready(send: ActorRef): Receive = {
    case Start => {
      snum = Utils.chooseSNum
      for(i <- 0 until Params.window) {
        val frame = Utils.encodeFrame((snum + i).asInstanceOf[Short], "This message is from Sender and you have received it correctly")
        self ! frame
      }
      println("waiting for ack")
      //Thread.sleep(1000)
      self ! StartTimer
    }
    case msg: ByteString => send ! Udp.Send(msg, remote)
    case StartTimer => system.scheduler.scheduleOnce(Params.dataSenderTimeOut, self, Timeout)
    case Timeout => {
      println("Sender Timed out")
      self ! CancelTimer
      self ! Retry
    }
    case Retry => {
      tries = tries + 1
      if(tries == Params.senderRetryTransmissions) self ! Kill
      println(s"try: $tries")
      self ! Start
    }
    case Kill =>  self ! PoisonPill
  }
}