package io.clouderite.orientdb

trait ObjectContext[T] {
  implicit val entityName: String
  implicit val orientContext: OrientContext
}
