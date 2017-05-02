package io.clouderite.orientdb

trait OrientContext {
  val databaseProtocol: String
  val databaseHost: String
  val databasePort: Int
  val databaseName: String
  val databaseLogin: String
  val databasePassword: String
  val databasePoolMin: Int = 2
  val databasePoolMax: Int = 10
  val entityPackageName: String
}

case class OrientContextCase(databaseProtocol: String, databaseHost: String, databasePort: Int, databaseName: String, databaseLogin: String, databasePassword: String, databasePoolMin: Int = 2, databasePoolMax: Int = 10, entityPackageName: String)

object OrientContextCase {
  implicit def toCase(oc: OrientContext): OrientContextCase = {
    OrientContextCase(oc.databaseProtocol, oc.databaseHost, oc.databasePort, oc.databaseName, oc.databaseLogin, oc.databasePassword, oc.databasePoolMin, oc.databasePoolMax, oc.entityPackageName)
  }
}
