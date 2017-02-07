package io.herdes.shared.orient

import com.orientechnologies.orient.core.record.impl.ODocument
import io.herdes.shared.orient.DocumentContext.{TD, TE}

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