package com.conmissio.temp

import com.conmissio.consumer.LoggingPostTransformer
import com.conmissio.{ConnectionConfig, MessageTransformer, RabbitMessageTransformer}
import com.typesafe.config.ConfigFactory

object Application extends App {

  val config = ConfigFactory.load
  val accountId: String = args(0)
  val connectionConfig = ConnectionConfig(uri = config.getString("transformer.rabbitmq.host"), port = config.getInt("transformer.rabbitmq.port"),
    queueName = config.getString("transformer.rabbitmq.message.queue"))
  // apply reverse function to each received message
  val transformationFunction = "override def apply(message: String): String = {\n      message.reverse\n    }"
  // create transformer
  val messageTransformer: MessageTransformer = RabbitMessageTransformer.newInstance(accountId, transformationFunction, new LoggingPostTransformer, connectionConfig)
  // start and connect to the queue
  messageTransformer.start()
  // temporary, only for testing purposes
  TempTransformerFunctionReloader.start(args(0), messageTransformer, connectionConfig)

}
