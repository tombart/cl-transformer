package com.conmissio.queues.client

import com.rabbitmq.client._
import org.slf4j
import org.slf4j.LoggerFactory

class RabbitMqExceptionHandler extends ExceptionHandler {

  private val LOGGER: slf4j.Logger = LoggerFactory.getLogger(this.getClass)

  override def handleFlowListenerException(channel: Channel, exception: Throwable): Unit = LOGGER.info("exception on handleFlowListenerException: " + exception)

  override def handleReturnListenerException(channel: Channel, exception: Throwable): Unit = LOGGER.info("exception on handleReturnListenerException: " + exception)

  override def handleConnectionRecoveryException(conn: Connection, exception: Throwable): Unit = LOGGER.info("Couldn't connect, reason: {}, retrying...", exception.getMessage)

  override def handleBlockedListenerException(connection: Connection, exception: Throwable): Unit = LOGGER.info("exception on handleBlockedListenerException: " + exception)

  override def handleChannelRecoveryException(ch: Channel, exception: Throwable): Unit = LOGGER.info("exception on handleChannelRecoveryException: " + exception)

  override def handleUnexpectedConnectionDriverException(conn: Connection, exception: Throwable): Unit = LOGGER.info("exception on handleUnexpectedConnectionDriverException: " + exception)

  override def handleConsumerException(channel: Channel, exception: Throwable, consumer: Consumer, consumerTag: String, methodName: String): Unit = LOGGER.info("exception on handleConsumerException: " + exception)

  override def handleTopologyRecoveryException(conn: Connection, ch: Channel, exception: TopologyRecoveryException): Unit = LOGGER.info("exception on handleTopologyRecoveryException: " + exception)

  override def handleConfirmListenerException(channel: Channel, exception: Throwable): Unit = LOGGER.info("exception on handleConfirmListenerException: " + exception)
}
