package com.nagarjuna_pamu

import akka.actor.Actor
import java.net.InetSocketAddress
import akka.io.IO
import akka.io.Udp
import akka.actor.ActorRef
import akka.util.ByteString

class SimpleSender(remote: InetSocketAddress) extends Actor {
  import context.system
  IO(Udp) ! Udp.SimpleSender
  
  def receive = {
    case Udp.SimpleSenderReady => 
      context.become(ready(sender))
  }
  
  def ready(send: ActorRef): Receive = {
    case msg: String =>
      send ! Udp.Send(ByteString(msg), remote)
  }
}