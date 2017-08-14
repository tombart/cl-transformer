package com.conmissio.consumer

trait MessageConsumer {

  def handle(message:String)

  def getId: String
}
