
This project is done in "Scala programming language"

library used Akka IO 

AKKA IO is a asynchronous non-blocking IO library 

AKKA is a open source toolkit and runtime for simplifying building concurrent applications on JVM and AKKA io is the asynchronous IO library part of the akka toolkit

1)Actor 

Actor can be used for implementing FINITE STATE MACHINES

Actor is a object which can do three things
1)communicate(send messages)
2)store(have state)
3)process(can execute code)

UDP sender is an Actor 
UDP receiver is also an Actor



This Project has following files

package com.nagarjuna_pamu.data_sender

Receiver.scala => Its an Actor which receives acks from the data receiver

Sender.scala => Its an Actor which sender data packets to the data receiver

package com.nagarjuna_pamu.data_receiver

Receiver.scala => Its an actor which receives data packets from the data sender

Sender.scala => Its an Actor which sends ack packets to the data receiver


package com.nagarjuna_pamu

Main.scala => Bootstrap program to start all the actors

Params.scala => Contains all parameters such as window size, timeout time etc

Utils.scala => Contains all methods for decoding , encoding packets and decoding , encoding packets .

to execute the project you must have java installed 

1) in the project folder run "./activator run"

2) or download sbt (simple build tool for scala) and in the project folder run "sbt run"