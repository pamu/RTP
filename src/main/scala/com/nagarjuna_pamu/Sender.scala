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
    case msg: String =>
      send ! Udp.Send(ByteString(msg), remote)
    /**
     * Actor do not know went to stop so, send it a Poison Pill to stop
     */
    case Kill => self ! PoisonPill
  }
}