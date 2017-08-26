package com.conmissio.queues

class MessageTransformerException(message: String) extends Exception {

  override def getMessage: String = message
}
