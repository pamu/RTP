package com.nagarjuna_pamu.data_receiver

import java.net.InetSocketAddress
import akka.io.IO
import akka.io.Udp
import akka.actor.Actor
import akka.actor.ActorRef
import akka.util.ByteString
import akka.actor.PoisonPill

case object Kill
class Sender(remote: InetSocketAddress) extends Actor {
  import context.system
  
  IO(Udp) ! Udp.SimpleSender
  
  def receive = {
    case Udp.SimpleSenderReady => context.become(ready(sender))
  }
  
  def ready(send: ActorRef): Receive = {
    case msg: String => send ! Udp.Send(ByteString.fromString(msg), remote)
    case Kill =>  self ! PoisonPill
  }
}