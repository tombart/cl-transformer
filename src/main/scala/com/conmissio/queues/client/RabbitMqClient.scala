package com.conmissio.queues.client

import java.io.IOException
import java.nio.charset.Charset

import com.conmissio.queues.ConnectionConfig
import com.conmissio.queues.compiler.FunctionCompilerError
import com.conmissio.queues.consumer.MessageConsumer
import com.rabbitmq.client._
import org.slf4j
import org.slf4j.LoggerFactory


class RabbitMqClient(var connectionConfig: ConnectionConfig, @volatile var messageConsumer: MessageConsumer) {

  private val LOGGER: slf4j.Logger = LoggerFactory.getLogger(this.getClass)
  var connection: Connection = _

  def connect(): Option[FunctionCompilerError] = {
    try {
      openConnection()
      Option.empty
    } catch {
      case ioe: IOException => Option(new FunctionCompilerError("", ioe))
    }
  }

  private def openConnection(): Unit = {
    val connectionFactory: ConnectionFactory = newConnectionFactory(connectionConfig)
    this.connection = connectionFactory.newConnection()
    val channel = connection.createChannel()
    addShutdownListener(connection)
    addRecoveryListener(channel)
    subscribeTo(channel)
  }

  private def newConnectionFactory(clientConfig: ConnectionConfig): ConnectionFactory = {
    this.connectionConfig = clientConfig
    val factory = new ConnectionFactory()
    factory.setHost(clientConfig.uri)
    factory.setPort(clientConfig.port)
    factory.setUsername(clientConfig.username)
    factory.setPassword(clientConfig.password)
    factory.setAutomaticRecoveryEnabled(true)
    factory.setTopologyRecoveryEnabled(true)
    factory.setExceptionHandler(new RabbitMqExceptionHandler)
    factory
  }

  private def addShutdownListener(connection: Connection): Unit = {
    connection.addShutdownListener((_: ShutdownSignalException) => {
      LOGGER.info("Connection {} has been shutdown", connectionConfig.connectionId)
    })
  }

  private def addRecoveryListener(channel: Channel): Unit = {
    channel.asInstanceOf[Recoverable].addRecoveryListener(new RecoveryListener {
      override def handleRecovery(recoverable: Recoverable): Unit = {
        LOGGER.info("Recovered connectionId: {}, details: {}", connectionConfig.connectionId.asInstanceOf[Any], recoverable.asInstanceOf[Any])
      }

      override def handleRecoveryStarted(recoverable: Recoverable): Unit = {
      }
    })
  }

  private def subscribeTo(channel: Channel) {
    val queue = createQueue(channel)
    channel.basicConsume(queue, true, createReceiver(channel))
  }

  private def createQueue(channel: Channel): String = {
    channel.queueDeclare(connectionConfig.queueName, connectionConfig.durable, connectionConfig.exclusive,
      connectionConfig.autoDelete, connectionConfig.arguments).getQueue
  }

  private def createReceiver(channel: Channel): Consumer = {
    new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: Array[Byte]) {
        val bodyAsString = new String(body, Charset.forName("UTF-8"))
        LOGGER.debug("Received message for accountId: {}, message: {}", messageConsumer.getId.asInstanceOf[Any], bodyAsString.asInstanceOf[Any])
        messageConsumer.handle(bodyAsString)
      }

      override def handleShutdownSignal(consumerTag: String, sig: ShutdownSignalException): Unit = {
        LOGGER.info("Shutting down connection: {}, reason: {}", connectionConfig.connectionId.asInstanceOf[Any], sig.getReason.asInstanceOf[Any])
      }
    }
  }

  def updateMessageConsumer(messageConsumer: MessageConsumer): Unit = {
    this.messageConsumer = messageConsumer
  }

  def reloadConnectionConfig(connectionConfig: ConnectionConfig): Option[FunctionCompilerError] = {
    try {
      this.closeConnection()
      this.connectionConfig = connectionConfig
      this.openConnection()
      Option.empty
    } catch {
      case ioe: IOException => Option(new FunctionCompilerError("", ioe))
    }
  }

  private def closeConnection(): Unit = {
    if (connection != null) connection.close()
  }

  def disconnect(): Option[FunctionCompilerError] = {
    try {
      closeConnection()
      Option.empty
    } catch {
      case ioe: IOException => Option(new FunctionCompilerError("", ioe))
    }
  }
}
