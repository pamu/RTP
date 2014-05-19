package com.nagarjuna_pamu.data_sender

import akka.actor.Actor
import akka.io.IO
import akka.io.Udp
import akka.actor.ActorRef
import java.net.InetSocketAddress
import com.nagarjuna_pamu.Params
import com.nagarjuna_pamu.Utils

class Receiver(senderSideSender: ActorRef) extends Actor {
  
  import context.system
  
  IO(Udp) ! Udp.Bind(self, new InetSocketAddress("127.0.0.1", Params.dataSenderSideBindingPort))
  
  def receive = {
    case Udp.Bound(local) => context.become(ready(sender))
  }
  
  def ready(socket: ActorRef): Receive = {
    case Udp.Received(frame, remote) => {
      println("got a ack")
      val tuple = Utils.decodeAck(frame)
      println("first snum: "+tuple._1)
      senderSideSender ! CancelTimer
    }
    case Udp.Unbind => self ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
  }
  
}