package com.conmissio.domain

import com.conmissio.TransformerClassFactory
import com.conmissio.client.ConnectionConfig
import com.typesafe.config.ConfigFactory

class Account(val id: String) {

  private val config = ConfigFactory.load

  def getMessageProcessor: Function[String, String] = {
    TransformerClassFactory.create(getStringClassBody, id)
  }

  def getConnectionConfig: ConnectionConfig = {
    ConnectionConfig(uri = config.getString("transformer.rabbitmq.host"), port = config.getInt("transformer.rabbitmq.port"),
      queueName = config.getString("transformer.rabbitmq.message.queue"))
  }

  def getStringClassBody: String = {
    //call http (labs) and get function for account id, for now default function
    "override def apply(v1: String): String = {\n    \"Got message: \" + v1\n  }\n"
  }
}
