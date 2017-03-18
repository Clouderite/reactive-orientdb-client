package com.devesion.orientdb

import com.devesion.orientdb.DocumentContext.{TD, TE}
import com.orientechnologies.orient.core.record.impl.ODocument

trait DocumentContext[T] {
  implicit def te: TE[T]
  implicit def td: TD[T]
  implicit val entityName: String
  implicit val orientContext: OrientContext
}

object DocumentContext {
  type TE[T] = (ODocument) => T
  type TD[T] = (T) => ODocument
}