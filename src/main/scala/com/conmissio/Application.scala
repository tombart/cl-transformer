package com.conmissio

import com.conmissio.MessageTransformer.MessageTransformer
import com.conmissio.temp.TempTransformerFunctionReloader

object Application extends App {

  /**
    * Start transformer and temp transformer function re-loader
    */
  val messageTransformer: MessageTransformer = MessageTransformer.newInstance(args(0))
  TempTransformerFunctionReloader.start(args(0), messageTransformer)
  messageTransformer.start()
}
