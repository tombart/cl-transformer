package com.conmissio.client

import java.util

case class ConnectionConfig(uri: String,
                            port: Int,
                            username: String = "guest",
                            password: String = "guest",
                            queueName:String,
                            durable: Boolean = false,
                            exclusive: Boolean = false,
                            autoDelete: Boolean = false,
                            arguments: util.Map[String, AnyRef] = null) {
}
