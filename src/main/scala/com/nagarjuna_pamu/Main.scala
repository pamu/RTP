package com.nagarjuna_pamu

import akka.actor.ActorSystem
import akka.actor.Props
import java.net.InetSocketAddress
import akka.io.UdpConnected

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
 
  val receiver = udpSystem.actorOf(Props[Receiver], "ReceiverActor")
  
  val sender = udpSystem.actorOf(Props(new SimpleSender(new InetSocketAddress("127.0.0.1", 9999))))
  
  def send(msg: String): Unit = {
    sender ! msg
  }
  while(true){
    Thread.sleep(1000)
    send("hello world")
  }
 
  Thread.sleep(Long.MaxValue) 
}