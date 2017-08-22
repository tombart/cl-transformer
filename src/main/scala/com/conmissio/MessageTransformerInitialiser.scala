package com.conmissio

trait MessageTransformerInitialiser {

  def getConnectionConfig: ConnectionConfig

  def getTransformer: Function[String, String]

  def getPostTransformer: PostTransformer

  def getAccountId: String
}
