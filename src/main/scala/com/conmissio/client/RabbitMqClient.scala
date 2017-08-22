package com.conmissio.client

import java.nio.charset.Charset

import akka.actor.{ActorRef, ActorSystem, Terminated}
import com.conmissio.ConnectionConfig
import com.conmissio.consumer.MessageConsumer
import com.newmotion.akka.rabbitmq.{BasicProperties, Channel, ChannelActor, ConnectionActor, ConnectionFactory, CreateChannel, Envelope}
import com.rabbitmq.client.{Consumer, DefaultConsumer}
import org.slf4j
import org.slf4j.LoggerFactory

import scala.concurrent.Future

class RabbitMqClient(var connectionConfig: ConnectionConfig, @volatile var messageConsumer: MessageConsumer)  {

  private implicit val system = ActorSystem()
  private val LOGGER: slf4j.Logger = LoggerFactory.getLogger(this.getClass)

  var connection:ActorRef = _

  def start(): Unit = {
    connection = system.actorOf(ConnectionActor.props(newConnectionFactory(connectionConfig)), "rabbitmq")
    connection ! CreateChannel(ChannelActor.props(setupSubscriber), Some("subscriber"))
  }

  def reloadConnectionConfig(connectionConfig: ConnectionConfig): Unit = {
    this.stop()
    this.connectionConfig = connectionConfig
    this.start()
  }

  def updateMessageConsumer(messageConsumer: MessageConsumer): Unit = {
    this.messageConsumer = messageConsumer
  }

  private def newConnectionFactory(clientConfig: ConnectionConfig): ConnectionFactory = {
    this.connectionConfig = clientConfig
    val factory = new ConnectionFactory()
    factory.setHost(clientConfig.uri)
    factory.setPort(clientConfig.port)
    factory.setUsername(clientConfig.username)
    factory.setPassword(clientConfig.password)
    factory
  }

  private def setupSubscriber(channel: Channel, self: ActorRef) {
    val exchange = "amq.fanout"
    val queue = createQueue(channel)
    channel.queueBind(queue, exchange, "")
    channel.basicConsume(queue, true, createReceiver(channel))
  }

  private def createQueue(channel: Channel): String = {
    channel.queueDeclare(connectionConfig.queueName, connectionConfig.durable, connectionConfig.exclusive,
      connectionConfig.autoDelete, connectionConfig.arguments).getQueue
  }

  private def createReceiver(channel: Channel): Consumer = {
    new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
        val bodyAsString = new String(body, Charset.forName("UTF-8"))
        LOGGER.debug("Received message for account: {}, message: {}", messageConsumer.getId.asInstanceOf[Any], bodyAsString.asInstanceOf[Any])
        messageConsumer.handle(bodyAsString)
      }
    }
  }

  def stop(): Unit = {
    system stop connection
    val future: Future[Terminated] =  system.terminate()
    future.wait(3000)
  }
}
