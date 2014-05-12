package com.nagarjuna_pamu

import akka.actor.Actor
import akka.io.UdpConnected
import akka.io.IO
import java.net.InetSocketAddress
import akka.actor.ActorRef
import akka.util.ByteString

/**
 * Sender actor helps in sending the datagrams (messages) to the Receiver Actor
 */
class Sender(remote: InetSocketAddress) extends Actor {
  /**
   * import the system to add IO manager into scope
   */
  import context.system
  /**
   * Connected does not mean that we are using connection oriented protocol, but this is 
   * only to send to the remoteAddress it was connected to, that it will receive datagrams only from that address.
   */
  IO(UdpConnected) ! UdpConnected.Connect(self, remote)
  /**
   * Actor needs a receive message to be implemented
   * Receive method here helps us to send messages (datagrams to the receiver actor)
   */
  def receive = {
    /**
     * Warning: Connect does not mean that we are using connection oriented protocol
     */
    case UdpConnected.Connect => {
      /**
       * become puts the ready method on the top of the actors message queue to be executed first
       */
      context.become(ready(sender))
    }
    /**
     * ready is real sender that sends messages to receiver actor
     */
    def ready(receiverActor: ActorRef): Receive = {
      /**
       * data receiver by sender actor on connection
       */
      case UdpConnected.Received(data) => {
        println("received pseudo connection data : "+data)
      }
      /**
       * send the message
       */
      case msg: String => {
        receiverActor ! UdpConnected.Send(ByteString(msg))
      }
      /**
       * Warning: Disconnection doesn't mean that we are using connection oriented protocol
       * ask the receiver to stop after we disconnect
       */
      case d @ UdpConnected.Disconnect => {
        receiverActor ! d 
      }
      /**
       * stop the actor on disconnection
       */
      case UdpConnected.Disconnected => {
        context.stop(self)
      }
    }
  }
}