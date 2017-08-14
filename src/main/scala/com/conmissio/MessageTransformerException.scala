package com.conmissio

class MessageTransformerException(message: String) extends Exception {

  override def getMessage: String = message
}
