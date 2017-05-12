package io.clouderite.orientdb

import java.util

import com.orientechnologies.orient.core.record.ORecord
import com.orientechnologies.orient.core.record.impl.ODocument
import io.clouderite.orientdb.DocumentContext.{TD, TE, TN}
import spray.json.{JsonFormat, pimpString}

import scala.collection.JavaConverters._
import scala.reflect.runtime.universe._

abstract class DocumentContext[T: TypeTag] {
  def mapper = new EntityMapper[T]()

  implicit def te: TE[T]
  implicit def td: TD[T]
  implicit def tn: TN[T] = mapper.entityToName

  implicit val orientContext: OrientContext
}

abstract class JsonDocumentContext[T : TypeTag] extends DocumentContext[T] {
  implicit val jsonFormat: JsonFormat[T]
  override def mapper = new JsonEntityMapper[T]()

  implicit def te: TE[T] = mapper.documentToEntity
  implicit def td: TD[T] = mapper.entityToDocument

  implicit def documentToDocumentOperations(doc: ODocument): DocumentOperations = new DocumentOperations(doc)

  class DocumentOperations(ret: ODocument) {
    def extractList[B : JsonFormat](field: String): List[B] = {
      val optionFieldDocs: Option[util.List[ORecord]] = ret.optionField(field)

      optionFieldDocs.map { fieldDocs ⇒
        ret.field(field, Nil.asJava)

        for {
          block ← fieldDocs.asScala.toList
          rec = block.getRecord[ODocument]
          j = rec.toJSON
          p = j.parseJson
          o = p.convertTo[B]
        } yield o

      }.getOrElse(List.empty)
    }

    def extractListAsSet[B : JsonFormat](field: String): Set[B] = {
      extractList[B](field).toSet
    }

    def extractListSimple[B](field: String): List[B] = {
      val optionFieldDocs: Option[util.List[B]] = ret.optionField(field)

      optionFieldDocs.map { fieldDocs ⇒
        ret.field(field, Nil.asJava)
        fieldDocs.asScala.toList
      }.getOrElse(List.empty)
    }

    def extractListAsSetSimple[B](field: String): Set[B] = {
      extractListSimple[B](field).toSet
    }

    def extractListNested[B](field: String)(implicit context: DocumentContext[B]): List[B] = {
      val optionFieldDocs: Option[util.List[ORecord]] = ret.optionField(field)

      optionFieldDocs.map { fieldDocs ⇒
        ret.field(field, Nil.asJava)

        for {
          block ← fieldDocs.asScala.toList
          rec = block.getRecord[ODocument]
          o = context.te(rec)
        } yield o
      }.getOrElse(List.empty)
    }

    def injectListSimple[B](field: String, entities: List[B]): Unit = {
      val docs = entities.asJava

      ret.field(field, docs)
    }

    def injectSetAsListSimple[B](field: String, entities: Set[B]): Unit = {
      val docs = entities.toList.asJava

      ret.field(field, docs)
    }

    def injectListNested[B](field: String, entities: List[B])(implicit context: DocumentContext[B]): Unit = {
      val docs =
        entities
          .map(context.td)
          .asJava

      ret.field(field, docs)
    }

    def injectListRead[B <: Entity[String]](field: String, entities: List[B])(implicit context: DocumentContext[B]): Unit = {
      val repository = DocumentRepository(context)
      val docs =
        entities
          .map(_.id)
          .map(repository.findDocumentById)
          .asJava

      ret.field(field, docs)
    }

    def optionField[RET](name: String): Option[RET] = {
      if (ret.fieldNames().contains(name)) {
        Some(ret.field(name))
      } else {
        None
      }
    }
  }
}

object DocumentContext {
  type TE[T] = (ODocument) => T
  type TD[T] = (T) => ODocument
  type TN[T] = () => String
}