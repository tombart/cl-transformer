package com.conmissio.queues

import com.conmissio.queues.client.RabbitMqClient
import com.conmissio.queues.compiler.{FunctionCompiler, FunctionCompilerError}
import com.conmissio.queues.consumer.TransformingMessageConsumer
import com.conmissio.queues.postTransformer.PostTransformer

object RabbitMqMessageTransformer {

  def newInstance(accountId: String,
                  transformer: String,
                  postTransformer: PostTransformer,
                  connectionConfig: ConnectionConfig): RabbitMessageTransformer = {
    FunctionCompiler.toClass(transformer, accountId) match {
      case Left(function) => new RabbitMessageTransformer(accountId, function, postTransformer, connectionConfig)
      case Right(transformerError) => throw new MessageTransformerException(transformerError.toString)
    }
  }

  class RabbitMessageTransformer(accountId: String,
                                 transformer: Function[String, String],
                                 postTransformer: PostTransformer,
                                 connectionConfig: ConnectionConfig) extends MessageTransformer {

    private val rabbitMqClient: RabbitMqClient = new RabbitMqClient(connectionConfig, new TransformingMessageConsumer(accountId, transformer, postTransformer))
    @volatile private var running: Boolean = false

    override def start(): Option[FunctionCompilerError] = {
      if (running) return Option.empty
      running = true
      rabbitMqClient.connect()
    }

    override def stop(): Option[FunctionCompilerError] = {
      if (!running) return Option.empty
      val result = rabbitMqClient.disconnect()
      running = false
      result
    }

    override def updateTransformer(transformingFunction: String): Option[FunctionCompilerError] = {
      FunctionCompiler.toClass(transformingFunction, accountId) match {
        case Left(function) => rabbitMqClient.updateMessageConsumer(new TransformingMessageConsumer(accountId, function, postTransformer))
          Option.empty
        case Right(transformerError) => Option(transformerError)
      }
    }

    override def updatePostTransformer(postTransformer: PostTransformer): Unit = {
      rabbitMqClient.updateMessageConsumer(new TransformingMessageConsumer(accountId, transformer, postTransformer))
    }

    override def reloadConnectionConfigAndRestart(connectionConfig: ConnectionConfig): Option[FunctionCompilerError] = {
      rabbitMqClient.reloadConnectionConfig(connectionConfig)
    }

    override def getId: String = {
      accountId
    }

    override def isRunning: Boolean = running
  }

}
