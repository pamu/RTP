package com.nagarjuna_pamu.data_receiver

import akka.actor.Actor
import akka.io.IO
import akka.io.Udp
import akka.actor.ActorRef
import java.net.InetSocketAddress
import com.nagarjuna_pamu.Params
import com.nagarjuna_pamu._

class Receiver(receiverSideSender: ActorRef) extends Actor {
  var startSNum = -1
  var msg: String = null
  var count = 0
  var flag: Boolean = false
  val bitmap = Array.fill[Byte](Params.window)(0)
  import context.system
  
  IO(Udp) ! Udp.Bind(self, new InetSocketAddress("127.0.0.1", Params.dataReceiverSideBindingPort))
  
  def receive = {
    case Udp.Bound(local) => context.become(ready(sender))
  }
  
  def ready(socket: ActorRef): Receive = {
    case Udp.Received(frame, remote) => {
      val tuple = Utils.decodeFrame(frame)
      if(count == 0) {
        startSNum = tuple._1
        msg = new String(tuple._2)
        println("got first msg containing: "+msg)
      }
      val diff = tuple._1 - startSNum
      if(diff < Params.window)
      if(diff <= Params.window){
    	  bitmap(diff) = 1
      }
      count = count + 1
      println(tuple._1+" received")
      for(i <- bitmap)
      print(s"$i ")
      println
      if(diff == Params.window - 1){
        receiverSideSender ! Utils.encodeAck(startSNum.asInstanceOf[Short],new String(Utils.md5(msg)), bitmap)
        for(i <- 0 until bitmap.size){
          bitmap(i) = 0
        }
        count = 0
      } 
    }
    case Udp.Unbind => socket ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
  }
  
}