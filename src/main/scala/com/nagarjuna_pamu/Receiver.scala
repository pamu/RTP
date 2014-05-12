package com.nagarjuna_pamu

import akka.actor.ActorRef
import akka.actor.Actor
import akka.io.IO
import akka.io.Udp
import java.net.InetSocketAddress

/**
 * Receiver actor receives datagrams from the sender actor
 */
class Receiver(senderActor: ActorRef) extends Actor {
  /**
   * import context.system to get access to IO manager
   */
  import context.system
  /**
   * IO manager takes Udp as argument
   * port 0 means operating system allocates available free port to bind
   */
  IO(Udp) ! Udp.Bind(self, new InetSocketAddress("localhost", 0))
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
      context.become(ready(sender))
    }
    /**
     * defining ready method
     */
    def ready(senderActor: ActorRef): Receive = {
      /**
       * Receive the data(datagrams sent by the sender)
       */
      case Udp.Received(data, remote) => {
        
      }
      /**
       * Send the sender of the datagrams the unbind message
       */
      case Udp.Unbind => {
        senderActor ! Udp.Unbind
      }
      /**
       * stop the actor on unbound
       */
      case Udp.Unbound => {
        context.stop(self)
      }
    }
  }
}