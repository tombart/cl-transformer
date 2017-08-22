package com.conmissio.consumer

import com.conmissio.PostTransformer
import org.slf4j
import org.slf4j.LoggerFactory

class LoggingPostTransformer extends PostTransformer {

  private val LOGGER: slf4j.Logger = LoggerFactory.getLogger(this.getClass)

  override def invoke(processedMessage: String): Unit = {
    LOGGER.info("Transformed Message: {}", processedMessage)
  }
}
