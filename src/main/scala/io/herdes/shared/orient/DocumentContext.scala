package io.herdes.shared.orient

import com.orientechnologies.orient.core.record.impl.ODocument

trait DocumentContext[T] {
  type TE = (ODocument) => T
  type TD = (T) => ODocument

  implicit def te: TE
  implicit def td: TD
  implicit val entityName: String
  implicit val orientContext: OrientContext
}
