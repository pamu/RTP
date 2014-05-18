package com.nagarjuna_pamu

import akka.util.ByteString
import java.security.MessageDigest

object Utils {
 
 def decode(frame: ByteString) = {
  /**
   * 
   */
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
 
  /**
   * MD5
   */
  def md5(s: String) = {
    MessageDigest.getInstance("MD5").digest(s.getBytes)
  }
}