package com.conmissio

import com.conmissio.client.RabbitMqClient
import com.conmissio.consumer.TransformingMessageConsumer
import com.conmissio.temp.TempTransformerFunctionReloader

object MessageTransformer extends App {

  private var messageConsumer: TransformingMessageConsumer = _
  private var account: Account = _

  /**
    * Start transformer and temp transformer function re-loader
    */
  MessageTransformer.start(args(0))
  TempTransformerFunctionReloader.start(args(0))

  def start(accountId: String): Unit = {
    account = new Account(accountId)
    messageConsumer = new TransformingMessageConsumer(account.id, account.getMessageProcessor)
    RabbitMqClient.start(account.getConnectionConfig, messageConsumer)
  }

  def stop(accountId: String): Unit = {
    RabbitMqClient.stop()
  }

  def reloadTransformerFunction(accountId: String): Unit = {
    setTransformerFunction(accountId, account.getMessageProcessor)
  }

  def setTransformerFunction(accountId: String, transformerFunction: Function[String, String]): Unit = {
    if (!accountId.equals(account.id)) {
      throw new MessageTransformerException("Invalid accountId for instance of transformer, actual: " + accountId + ", expected: " + account.id)
    }
    messageConsumer.registerMessageProcessor(transformerFunction)
  }

  def getProcessingAccountId: String = {
    account.id
  }
}
