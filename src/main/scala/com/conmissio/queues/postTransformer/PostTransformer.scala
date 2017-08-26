package com.conmissio.queues.postTransformer

trait PostTransformer {

  def invoke(processedMessage: String)
}
