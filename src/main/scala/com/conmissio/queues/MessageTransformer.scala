package com.conmissio.queues

import com.conmissio.queues.compiler.FunctionCompilerError
import com.conmissio.queues.postTransformer.PostTransformer

trait MessageTransformer {

  /**
    * Start queue client and connect to the queue.
    */
  def start(): Option[FunctionCompilerError]

  /**
    * Stop queue client and disconnect from the queue.
    */
  def stop(): Option[FunctionCompilerError]

  /**
    * Update transformation logic to apply to received messages. Transforming function should be passed as string in format:
    *
    *     override def apply(message: String): String = {
    *       message.reverse
    *      }
    *
    * @param transformingFunction
    */
  def updateTransformer(transformingFunction: String): Option[FunctionCompilerError]

  /**
    * Update logic to be applied after transformation executed. Message passed to postTransformer is processed message.
    * Whatever came out from Transformer is passed to postTransformer.
    *
    * @param postTransformer
    */
  def updatePostTransformer(postTransformer: PostTransformer)

  /**
    * Update queue connection details and restart.
    *
    * @param connectionConfig
    */
  def reloadConnectionConfigAndRestart(connectionConfig: ConnectionConfig): Option[FunctionCompilerError]

  /**
    * Get owning account id.
    * @return
    */
  def getId: String

  /**
    * Check weather queue client is running and connected.
    *
    * @return
    */
  def isRunning: Boolean
}
