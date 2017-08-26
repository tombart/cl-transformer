
organization := "com.conmissio"
version := "1.0.1"
isSnapshot := true
name := "competition-queues"
scalaVersion := "2.12.3"
crossPaths := false

libraryDependencies += "com.rabbitmq" % "amqp-client" % "4.2.0"
libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.12.3"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"