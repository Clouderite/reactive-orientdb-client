package io.herdes.shared.orient

trait ObjectContext[T] {
  implicit val entityName: String
  implicit val orientContext: OrientContext
}
