package com.nagarjuna_pamu

import akka.actor.ActorSystem
import akka.actor.Props
import java.net.InetSocketAddress
import akka.io.UdpConnected
import akka.util.ByteString
import com.nagarjuna_pamu.data_sender._

/**
 * App stands for Application which provides main method and is the application entry of the project
 */
object Main extends App {
  /**
   * Application entry point
   */
  println("Application started")
  /**
   * get the reference to actor system
   */
  val system = ActorSystem("UdpSystem")
  val receiverSideSender = system.actorOf(Props(new com.nagarjuna_pamu.data_receiver.Sender(new InetSocketAddress("127.0.0.1", Params.dataSenderSideBindingPort))), "ReceiverSideSender")
  val receiverSideReceiver = system.actorOf(Props(new com.nagarjuna_pamu.data_receiver.Receiver(receiverSideSender)))
  val senderSideSender = system.actorOf(Props(new com.nagarjuna_pamu.data_sender.Sender(new InetSocketAddress("127.0.0.1", Params.dataReceiverSideBindingPort))), "SenderSideSender")
  val senderSiderReceiver = system.actorOf(Props(new com.nagarjuna_pamu.data_sender.Receiver(senderSideSender)))
  
  Thread.sleep(2000)
  
  senderSideSender ! Start
  
  Thread.sleep(Long.MaxValue)
}