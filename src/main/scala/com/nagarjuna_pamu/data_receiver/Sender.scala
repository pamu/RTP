package com.nagarjuna_pamu.data_receiver

import java.net.InetSocketAddress
import akka.io.IO
import akka.io.Udp
import akka.actor.Actor
import akka.actor.ActorRef
import akka.util.ByteString
import akka.actor.PoisonPill

case object Kill
case class Ack(frame: ByteString)
class Sender(remote: InetSocketAddress) extends Actor {
  import context.system
  
  IO(Udp) ! Udp.SimpleSender
  
  def receive = {
    case Udp.SimpleSenderReady => context.become(ready(sender))
  }
  
  def ready(send: ActorRef): Receive = {
    case msg: ByteString => send ! Udp.Send(msg, remote)
    case Ack(frame) => {println("ack received");self ! frame}
    case Kill =>  self ! PoisonPill
  }
}