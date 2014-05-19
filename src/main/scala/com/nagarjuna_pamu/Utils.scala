package com.nagarjuna_pamu

import akka.util.ByteString
import java.security.MessageDigest

object Utils {
 def chooseSNum = scala.util.Random.nextInt(Short.MaxValue/2)
 def decodeFrame(frame: ByteString) = {
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

 def encodeFrame(counter: Short, msg: String) = {
    implicit val byteOrder = java.nio.ByteOrder.BIG_ENDIAN
    val frameBuilder = ByteString.newBuilder
    frameBuilder.putShort(counter)
    if(msg.length < 508){
    	frameBuilder.putShort(msg.length())
    }else{
    	frameBuilder.putShort(508)
    }
    frameBuilder.putBytes(msg.take(508).getBytes())
    frameBuilder.result
  }
 
  def encodeAck(snum: Short, msg: String, bitmap: Array[Byte]): ByteString = {
    implicit val byteOrder = java.nio.ByteOrder.BIG_ENDIAN
    val frameBuilder = ByteString.newBuilder
    frameBuilder.putShort(snum)
    frameBuilder.putBytes(md5(msg))
    frameBuilder.putBytes(bitmap)
    frameBuilder.result
  }
  
  def decodeAck(ack: ByteString) = {
    implicit val byteOrder = java.nio.ByteOrder.BIG_ENDIAN
    val in = ack.iterator
    val snum = in.getShort
    val md5 = Array.newBuilder[Byte]
    for(i <- 1 to 16) {
      md5 += in.getByte
    }
    val bitmap = Array.newBuilder[Byte]
    for(i <- 1 to Params.window) {
      bitmap += in.getByte
    }
    (snum, md5, bitmap)
  }
 
  /**
   * MD5
   */
  def md5(s: String) = {
    MessageDigest.getInstance("MD5").digest(s.getBytes)
  }
}