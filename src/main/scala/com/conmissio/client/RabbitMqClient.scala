package com.conmissio.client

import java.nio.charset.Charset

import akka.actor.{ActorRef, ActorSystem}
import com.conmissio.consumer.MessageConsumer
import com.newmotion.akka.rabbitmq.{BasicProperties, Channel, ChannelActor, ConnectionActor, ConnectionFactory, CreateChannel, Envelope}
import com.rabbitmq.client.{Consumer, DefaultConsumer}
import org.slf4j
import org.slf4j.LoggerFactory

class RabbitMqClient  {

  private implicit val system = ActorSystem()
  private val LOGGER: slf4j.Logger = LoggerFactory.getLogger(this.getClass)

  var connection:ActorRef = _
  var clientConfig: ConnectionConfig = _
  var messageConsumer: MessageConsumer = _

  def start(clientConfig: ConnectionConfig, messageConsumer: MessageConsumer): Unit = {
    this.messageConsumer = messageConsumer
    connection = system.actorOf(ConnectionActor.props(newConnectionFactory(clientConfig)), "rabbitmq")
    connection ! CreateChannel(ChannelActor.props(setupSubscriber), Some("subscriber"))
  }

  private def newConnectionFactory(clientConfig: ConnectionConfig): ConnectionFactory = {
    this.clientConfig = clientConfig
    val factory = new ConnectionFactory()
    factory.setHost(clientConfig.uri)
    factory.setPort(clientConfig.port)
    factory.setUsername(clientConfig.username)
    factory.setPassword(clientConfig.password)
    factory
  }

  def stop(): Unit = {
    system stop connection
    system.terminate()
  }

  private def setupSubscriber(channel: Channel, self: ActorRef) {
    val exchange = "amq.fanout"
    val queue = createQueue(channel)
    channel.queueBind(queue, exchange, "")
    channel.basicConsume(queue, true, createReceiver(channel))
  }

  private def createQueue(channel: Channel): String = {
    channel.queueDeclare(clientConfig.queueName, clientConfig.durable, clientConfig.exclusive,
      clientConfig.autoDelete, clientConfig.arguments).getQueue
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
}
