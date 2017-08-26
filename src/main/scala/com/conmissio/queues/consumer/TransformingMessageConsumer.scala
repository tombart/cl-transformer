package com.conmissio.queues.consumer

import com.conmissio.queues.postTransformer.PostTransformer

class TransformingMessageConsumer(accountId: String,
                                  messageProcessor: Function[String, String] = (x:String) => x,
                                  postTransformer: PostTransformer) extends MessageConsumer {

  override def handle(message: String): Unit = {
    val processedMessage: String = messageProcessor.apply(message)
    postTransformer.invoke(processedMessage)
  }

  override def getId: String = {
    accountId
  }
}
