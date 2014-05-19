package com.nagarjuna_pamu.data_sender

import akka.actor.Actor
import akka.io.IO
import akka.io.Udp
import akka.actor.ActorRef
import java.net.InetSocketAddress
import com.nagarjuna_pamu.Params
import com.nagarjuna_pamu.Utils
case class WindowStart(start: Int)
class Receiver(senderSideSender: ActorRef) extends Actor {
  var loss = 0
  import context.system
  
  IO(Udp) ! Udp.Bind(self, new InetSocketAddress("127.0.0.1", Params.dataSenderSideBindingPort))
  
  def receive = {
    case Udp.Bound(local) => context.become(ready(sender))
  }
  
  def ready(socket: ActorRef): Receive = {
    case Udp.Received(frame, remote) => {
      println("got a ack and its time to send next window")
      val tuple = Utils.decodeAck(frame)
      println("first snum: "+tuple._1)
      val bitmap = tuple._3
      bitmap.foreach(x => print(s"$x "))
      println
      bitmap.foreach(x => if(x == 0){ loss = loss + 1})
      
      senderSideSender ! CancelTimer
      senderSideSender ! WindowStart((loss/bitmap.length)*100)
      loss = 0
    }
    case Udp.Unbind => self ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
  }
  
}