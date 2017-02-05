package io.herdes.shared.orient

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
