package com.nagarjuna_pamu

import scala.concurrent.duration._

object Params {
  var dataReceiverSideBindingPort = 9999
  var dataSenderSideBindingPort = 8888
  var window = 20
  var loss = 5
  var dataSenderTimeOut = 50 milliseconds
  var senderRetryTransmissions = 1
  var senderLog = "sender.log"
  var receiverLog = "receiver.log"
}