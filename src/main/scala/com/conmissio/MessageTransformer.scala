package com.conmissio

trait MessageTransformer {

  /**
    * Start queue client and connect to the queue.
    */
  def start()

  /**
    * Stop queue client and disconnect from the queue.
    */
  def stop()

  /**
    * Update transformation logic to apply to received messages. Transforming function should be passed as string in format:
    *
    *     override def apply(message: String): String = {
    *       message.reverse
    *      }
    *
    * @param transformingFunction
    */
  def updateTransformer(transformingFunction: String)

  /**
    * Update transformation logic to apply to received messages.
    *
    * @param transformer
    */
  def updateTransformer(transformer: Function[String, String])

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
  def reloadConnectionConfig(connectionConfig: ConnectionConfig)

  /**
    * Get owning account id.
    * @return
    */
  def getAccountId: String

  /**
    * Check weather queue client is running and connected.
    *
    * @return
    */
  def isRunning: Boolean
}
