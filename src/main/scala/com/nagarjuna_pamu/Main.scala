package com.nagarjuna_pamu

import akka.actor.ActorSystem
import akka.actor.Props
import java.net.InetSocketAddress
import akka.io.UdpConnected
import akka.util.ByteString

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
  val udpSystem = ActorSystem("UdpSystem")
 
  /**
   * create the receiver actor
   */
  val receiver = udpSystem.actorOf(Props[Receiver], "ReceiverActor")
  
  /**
   * create the sender actor
   */
  val sender = udpSystem.actorOf(Props(new SimpleSender(new InetSocketAddress("127.0.0.1", 9999))))
  
  /**
   * 
   */
  while(true) {
    Thread.sleep(1000)
    sender ! "non-blocking io rocks"
  }
  
  /**
   * wait don't quit the main thread
   */
  Thread.sleep(Long.MaxValue) 
}