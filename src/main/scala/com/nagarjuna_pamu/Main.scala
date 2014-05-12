package com.nagarjuna_pamu

import akka.actor.ActorSystem

/**
 * App stands for Application which provides main method and is the application entry of the project
 */
class Main extends App {
  /**
   * Application entry point
   */
  println("Application started")
  /**
   * get the reference to actor system
   */
  implicit val udpSystem = ActorSystem("UdpSystem")
  /**
   * 
   */
}