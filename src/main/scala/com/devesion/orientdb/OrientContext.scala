package com.devesion.orientdb

trait OrientContext {
  val databaseProtocol: String
  val databaseHost: String
  val databaseName: String
  val databaseLogin: String
  val databasePassword: String
  val databasePoolMin: Int = 2
  val databasePoolMax: Int = 10
  val entityPackageName: String
}

case class OrientContextCase(databaseProtocol: String, databaseHost: String, databaseName: String, databaseLogin: String, databasePassword: String, databasePoolMin: Int = 2, databasePoolMax: Int = 10, entityPackageName: String)

object OrientContextCase {
  implicit def toCase(oc: OrientContext): OrientContextCase = {
    OrientContextCase(oc.databaseProtocol, oc.databaseHost, oc.databaseName, oc.databaseLogin, oc.databasePassword, oc.databasePoolMin, oc.databasePoolMax, oc.entityPackageName)
  }
}
