package com.conmissio.temp

import java.nio.charset.Charset

import akka.actor.{ActorRef, ActorSystem}
import com.conmissio.compiler.TransformerClassFactory
import com.conmissio.{ConnectionConfig, MessageTransformer}
import com.newmotion.akka.rabbitmq.{BasicProperties, Channel, ChannelActor, ConnectionActor, ConnectionFactory, CreateChannel, Envelope}
import com.rabbitmq.client.{Consumer, DefaultConsumer}
import org.slf4j
import org.slf4j.LoggerFactory

// Temporary object to listen to a rabbit mq and reload transforming function
object TempTransformerFunctionReloader  {

  private implicit val system = ActorSystem()
  private val LOGGER: slf4j.Logger = LoggerFactory.getLogger(TempTransformerFunctionReloader.getClass)

  var connection:ActorRef = _
  var clientConfig: ConnectionConfig = _
  var messageTransformer: MessageTransformer = _
  var accountId: String = _
  def start(accountId: String, messageTransformer: MessageTransformer, connectionConfig: ConnectionConfig): Unit = {
    this.accountId = accountId
    this.messageTransformer = messageTransformer;
    clientConfig = connectionConfig
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
    channel.queueDeclare("classBodyThatExtendsFunction", clientConfig.durable, clientConfig.exclusive,
      clientConfig.autoDelete, clientConfig.arguments).getQueue
  }

  private def createReceiver(channel: Channel): Consumer = {
    new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String, envelope: Envelope, properties: BasicProperties, body: Array[Byte]) {
        val stringFunction = new String(body, Charset.forName("UTF-8"))
        LOGGER.debug("Reloading function for account: {}, function: {}", accountId.asInstanceOf[Any], stringFunction.asInstanceOf[Any])
        messageTransformer.updateTransformer(TransformerClassFactory.create(stringFunction, accountId))
      }
    }
  }
}
