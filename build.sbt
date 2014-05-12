name := """RTP"""

version := "1.0"

scalaVersion := "2.10.2"

mainClass := Some("com.nagarjuna_pamu.Main")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.1"
)
