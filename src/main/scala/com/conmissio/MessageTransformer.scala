package com.conmissio

import com.conmissio.client.RabbitMqClient
import com.conmissio.consumer.TransformingMessageConsumer
import com.conmissio.domain.Account

object MessageTransformer {

  def newInstance(accountId: String): MessageTransformer = {
    new MessageTransformer(accountId)
  }

class MessageTransformer(accountId: String) {

  private val account: Account = new Account(accountId)
  private val rabbitMqClient : RabbitMqClient = new RabbitMqClient
  private val messageConsumer: TransformingMessageConsumer = new TransformingMessageConsumer(account.id, account.getMessageProcessor)
  @volatile private var running : Boolean = false

  def start(): Unit = {
    if (running) return
    running = true
    rabbitMqClient.start(account.getConnectionConfig, messageConsumer)
  }

  def stop(): Unit = {
    if (!running) return
    rabbitMqClient.stop()
    running = false
  }

  def reloadTransformerFunction(): Unit = {
    setTransformerFunction(account.getMessageProcessor)
  }

  def setTransformerFunction(transformerFunction: Function[String, String]): Unit = {
    messageConsumer.registerMessageProcessor(transformerFunction)
  }

  def getProcessingAccountId: String = {
    account.id
  }

  def isRunning: Boolean = running
}
}
