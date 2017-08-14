package com.conmissio.consumer

import org.slf4j
import org.slf4j.LoggerFactory

class TransformingMessageConsumer(accountId: String, @volatile var messageProcessor: Function[String, String] = (x:String) => x) extends MessageConsumer {

  private val LOGGER: slf4j.Logger = LoggerFactory.getLogger(this.getClass)

  override def handle(message: String): Unit = {
    val processedMessage: String = messageProcessor.apply(message)
    LOGGER.debug("Transformed Message: {}", processedMessage)
  }

  def registerMessageProcessor(messageProcessor: Function[String, String] = (x:String) => x): Unit = {
    this.messageProcessor = messageProcessor
  }

  override def getId: String = {
    accountId
  }
}
