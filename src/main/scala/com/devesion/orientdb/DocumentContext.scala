package com.devesion.orientdb

import com.devesion.orientdb.DocumentContext.{TD, TE, TN}
import com.orientechnologies.orient.core.record.impl.ODocument
import spray.json.JsonFormat

import scala.reflect.runtime.universe._

abstract class DocumentContext[T: TypeTag] {
  private def mapper = new EntityMapper[T]()

  implicit def te: TE[T]
  implicit def td: TD[T]
  implicit def tn: TN[T] = mapper.entityToName

  implicit val orientContext: OrientContext
}

abstract class JsonDocumentContext[T : TypeTag] extends DocumentContext[T] {
  implicit val jsonFormat: JsonFormat[T]
  private def mapper = new JsonEntityMapper[T]()

  implicit def te: TE[T] = mapper.documentToEntity
  implicit def td: TD[T] = mapper.entityToDocument
}

object DocumentContext {
  type TE[T] = (ODocument) => T
  type TD[T] = (T) => ODocument
  type TN[T] = () => String
}