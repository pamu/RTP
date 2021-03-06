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
case class NextWindow(startSNum: Int)
class Sender(remote: InetSocketAddress) extends Actor {
  import context.system
  var lastSNum = 0
  var currentSNum = 0
  var counter = 0
  var tries = 0
  var snum = 0
  var timer: akka.actor.Cancellable = null
  
  IO(Udp) ! Udp.SimpleSender
  
  def receive = {
    case Udp.SimpleSenderReady => context.become(ready(sender))
  }
  
  def ready(send: ActorRef): Receive = {
    case Start => {
      snum = Utils.chooseSNum
      currentSNum = snum
      self ! NextWindow(snum)
    }
    case NextWindow(start) => {
      val windowSize = Params.window
      if(counter == Params.packetsToSend){
        println(s"${Params.packetsToSend} packets sent")
        self ! Kill
      }
      lastSNum = currentSNum 
      for(i <- 0 until windowSize){
        currentSNum = start + i 
        val frame = Utils.encodeFrame((currentSNum).asInstanceOf[Short], "This message is from Sender and you have received it correctly")
        self ! frame
        counter = counter + 1
      }
      
      self ! StartTimer
    }
    case msg: ByteString => send ! Udp.Send(msg, remote)
    case StartTimer => timer = system.scheduler.scheduleOnce(Params.dataSenderTimeOut, self, Timeout)
    case Timeout => {
      self ! Kill
      self ! Retry
    }
    case CancelTimer => {
      if((timer != null && !timer.isCancelled)){
        timer.cancel
      }
      println("transmitted "+counter+" packets")
      
    }
    case Retry => {
      tries = tries + 1
      if(tries == Params.senderRetryTransmissions) self ! Kill
      self ! Start
    }
    case Kill =>  {
      self ! PoisonPill
      context.stop(self)
    }
    
    case Ack(ack) => {
    	val tuple = Utils.decodeAck(ack)
    	
    	if(tuple._1 == lastSNum){
    	  self ! NextWindow(currentSNum)
    	}else{
    		self ! NextWindow(lastSNum - 20)
    	}
    }
    
  }
}