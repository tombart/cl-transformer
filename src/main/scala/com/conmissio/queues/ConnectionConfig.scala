package com.conmissio.queues

import java.util

case class ConnectionConfig(connectionId: String,
                            uri: String,
                            port: Int,
                            username: String = "guest",
                            password: String = "guest",
                            queueName:String,
                            durable: Boolean = false,
                            exclusive: Boolean = false,
                            autoDelete: Boolean = false,
                            arguments: util.Map[String, AnyRef] = null) {
}
