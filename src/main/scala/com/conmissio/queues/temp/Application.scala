package com.conmissio.queues.temp

import com.conmissio.queues.postTransformer.LoggingPostTransformer
import com.conmissio.queues.{ConnectionConfig, MessageTransformer, RabbitMqMessageTransformer}

object Application extends App {

  val accountId: String = "1"
  val connectionConfig = ConnectionConfig(connectionId = "1", uri = "localhost", port = 5672, queueName = "feeds")
  // apply reverse function to each received message
  val transformationFunction = "override def apply(message: String): String = {\n      message.reverse\n    }"
  // create transformer
  val messageTransformer: MessageTransformer = RabbitMqMessageTransformer.newInstance("1", transformationFunction, new LoggingPostTransformer, connectionConfig)

  /*val connectionConfig2 = ConnectionConfig(connectionId = "2", uri = config.getString("transformer.rabbitmq.host"), port = config.getInt("transformer.rabbitmq.port"),
    queueName = "secondqueu")
  val messageTransformer2: MessageTransformer = RabbitMqMessageTransformer.newInstance("2", transformationFunction, new LoggingPostTransformer, connectionConfig2)

  val connectionConfig3 = ConnectionConfig(connectionId = "3", uri = config.getString("transformer.rabbitmq.host"), port = config.getInt("transformer.rabbitmq.port"),
    queueName = "thirdqueu")
  val messageTransformer3: MessageTransformer = RabbitMqMessageTransformer.newInstance("3", transformationFunction, new LoggingPostTransformer, connectionConfig3)
*/
  //val messageTransformer: MessageTransformer = RabbitMqMessageTransformer.newInstance(accountId, transformationFunction, new LoggingPostTransformer, connectionConfig)
  // start and connect to the queue
  messageTransformer.start()
  /*messageTransformer2.start()
  messageTransformer3.start()*/

  //messageTransformer.stop()

  println("FINISHED INIT")
  // temporary, only for testing purposes
  //TempTransformerFunctionReloader.start(args(0), messageTransformer, connectionConfig)

}
