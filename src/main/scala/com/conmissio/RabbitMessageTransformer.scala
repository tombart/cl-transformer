package com.conmissio

import com.conmissio.client.RabbitMqClient
import com.conmissio.compiler.TransformerClassFactory
import com.conmissio.consumer.TransformingMessageConsumer

object RabbitMessageTransformer {

  def newInstance(accountId: String,
                  transformer: Function[String, String],
                  postTransformer: PostTransformer,
                  connectionConfig: ConnectionConfig): RabbitMessageTransformer = {
    new RabbitMessageTransformer(accountId, transformer, postTransformer, connectionConfig)
  }

  def newInstance(accountId: String,
                  transformer: String,
                  postTransformer: PostTransformer,
                  connectionConfig: ConnectionConfig): RabbitMessageTransformer = {
    new RabbitMessageTransformer(accountId, TransformerClassFactory.create(transformer, accountId), postTransformer, connectionConfig)
  }

  def newInstance(initialiser: MessageTransformerInitialiser): RabbitMessageTransformer = {
    new RabbitMessageTransformer(initialiser.getAccountId, initialiser.getTransformer, initialiser.getPostTransformer, initialiser.getConnectionConfig)
  }

  class RabbitMessageTransformer(accountId: String,
                                 transformer: Function[String, String],
                                 postTransformer: PostTransformer,
                                 connectionConfig: ConnectionConfig) extends MessageTransformer {

    private val messageConsumer: TransformingMessageConsumer = new TransformingMessageConsumer(accountId, transformer, postTransformer)
    private val rabbitMqClient: RabbitMqClient = new RabbitMqClient(connectionConfig, messageConsumer)

    @volatile private var running: Boolean = false

    override def start(): Unit = {
      if (running) return
      running = true
      rabbitMqClient.start()
    }

    override def stop(): Unit = {
      if (!running) return
      rabbitMqClient.stop()
      running = false
    }

    override def updateTransformer(transformingFunction: String): Unit = {
      updateTransformer(TransformerClassFactory.create(transformingFunction, accountId))
    }

    override def updateTransformer(transformer: Function[String, String]): Unit = {
      rabbitMqClient.updateMessageConsumer(new TransformingMessageConsumer(accountId, transformer, postTransformer))
    }

    override def updatePostTransformer(postTransformer: PostTransformer): Unit = {
      rabbitMqClient.updateMessageConsumer(new TransformingMessageConsumer(accountId, transformer, postTransformer))
    }

    override def reloadConnectionConfig(connectionConfig: ConnectionConfig): Unit = {
      rabbitMqClient.reloadConnectionConfig(connectionConfig)
    }

    override def getAccountId: String = {
      accountId
    }

    override def isRunning: Boolean = running
  }

}
