package com.conmissio.queues.consumer

trait MessageConsumer {

  def handle(message:String)

  def getId: String
}
