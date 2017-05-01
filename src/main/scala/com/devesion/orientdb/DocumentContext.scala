package com.devesion.orientdb

import com.devesion.orientdb.DocumentContext.{TD, TE, TN}
import com.orientechnologies.orient.core.db.record.{ORecordLazyList, ORecordLazySet}
import com.orientechnologies.orient.core.record.impl.ODocument
import spray.json.{JsonFormat, pimpString}

import scala.collection.JavaConverters._
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

  implicit def documentToDocumentOperations(doc: ODocument): DocumentOperations = new DocumentOperations(doc)

  class DocumentOperations(ret: ODocument) {
    def extractList[B : JsonFormat](field: String): List[B] = {
      val fieldDocs: ORecordLazyList = ret.field(field)

      for {
        block ← fieldDocs.asScala.toList
        rec = block.getRecord[ODocument]
        j = rec.toJSON
        p = j.parseJson
        o = p.convertTo[B]
      } yield o
    }

    def extractSet[B : JsonFormat](field: String): Set[B] = {
      val fieldDocs: ORecordLazySet = ret.field(field)

      val values = for {
        block ← fieldDocs.asScala
        rec = block.getRecord[ODocument]
        j = rec.toJSON
        p = j.parseJson
        o = p.convertTo[B]
      } yield o

      Set.empty[B] ++ values
    }
  }
}

object DocumentContext {
  type TE[T] = (ODocument) => T
  type TD[T] = (T) => ODocument
  type TN[T] = () => String
}