package com.nagarjuna_pamu

import akka.actor.Actor
import java.net.InetSocketAddress
import akka.io.IO
import akka.io.Udp
import akka.actor.ActorRef
import akka.util.ByteString
import akka.actor.PoisonPill

/**
 * message to kill the simple sender
 */
case object Kill
/**
 * SimpleSender actor sends udp datagrams to the receiver
 */
class SimpleSender(remote: InetSocketAddress) extends Actor {
  var counter: Int = 0
  /**
   * import IO manager into scope
   */
  import context.system
  /**
   * register the Actor for network events
   */
  IO(Udp) ! Udp.SimpleSender
  
  /**
   * become method puts the ready method on the top of the queue for processing (Hotcode swap feature of actor)
   */
  def receive = {
    case Udp.SimpleSenderReady => 
      context.become(ready(sender))
  }
  
  /**
   * ready method
   */
  def ready(send: ActorRef): Receive = {
    /**
     * send the message to remote
     */
    case msg: String => {
     checkCounter
     val frame = getFrame(counter.asInstanceOf[Short], msg)
     counter = counter + 1
     send ! Udp.Send(frame, remote)
    }
    /**
     * Actor do not know went to stop so, send it a Poison Pill to stop
     */
    case Kill => self ! PoisonPill
  }
  
  /**
   * 
   */
  def checkCounter = {
    if(counter >= Short.MaxValue) counter = 0
  }
  
  /**
   * 
   */
  def getFrame(counter: Short, msg: String) = {
    implicit val byteOrder = java.nio.ByteOrder.BIG_ENDIAN
    val frameBuilder = ByteString.newBuilder
    frameBuilder.putShort(counter)
    if(msg.length < 508){
    	frameBuilder.putShort(msg.length())
    }else{
    	frameBuilder.putShort(508)
    }
    frameBuilder.putBytes(msg.take(508).getBytes())
    println(frameBuilder.length)
    frameBuilder.result
  }
}