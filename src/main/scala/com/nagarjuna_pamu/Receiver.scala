package com.nagarjuna_pamu

import akka.actor.ActorRef
import akka.actor.Actor
import akka.io.IO
import akka.io.Udp
import java.net.InetSocketAddress
import java.security.MessageDigest
import akka.util.ByteString

/**
 * Receiver actor receives datagrams from the sender actor
 */
class Receiver extends Actor {
  /**
   * import context.system to get access to IO manager
   */
  import context.system
  /**
   * Register with IO manager to get network events
   * IO manager takes Udp as argument
   * port 0 means operating system allocates available free port to bind
   */
  IO(Udp) ! Udp.Bind(self, new InetSocketAddress("127.0.0.1", 9999))
  /**
   * Actor's receive method(Every actor needs a receive method to be implemented)
   * receive method is a partial function
   */
  def receive = {
   /**
    * This code is invoked when Receiver is bound to the port
    */
    case Udp.Bound(local) => {
      /**
       * become method puts the ready message on the top of the message queue of the actor, 
       * so as to get executed first
       */
      println("receiver bound")
      context.become(ready(sender))
    }
    /**
     * defining ready method
     */
    def ready(socket: ActorRef): Receive = {
      /**
       * Receive the data(datagrams sent by the sender)
       */
      case Udp.Received(data, remote) => {
        val tuple = decode(data)
        println("seq no :  "+tuple._1)
        println("received data: "+new String(tuple._2))
      }
      /**
       * Send the sender of the datagrams the unbind message
       */
      case Udp.Unbind => {
        socket ! Udp.Unbind
      }
      /**
       * stop the actor on unbound
       */
      case Udp.Unbound => {
        context.stop(self)
        println("receiver stopped")
      }
    }
  }
  
  /**
   * MD5
   */
  def md5(s: String) = {
    MessageDigest.getInstance("MD5").digest(s.getBytes)
  }
  /**
   * 
   */
  def decode(frame: ByteString) = {
    implicit val byteOrder = java.nio.ByteOrder.BIG_ENDIAN
    val in = frame.iterator
    val short = in.getShort
    val len = in.getShort
    val a = Array.newBuilder[Byte]
    for(i <- 1 to len) {
      a += in.getByte
    }
    (short, a.result)
  }
}