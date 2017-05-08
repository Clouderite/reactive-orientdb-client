package io.clouderite.orientdb

import com.orientechnologies.orient.core.record.impl.ODocument
import spray.json.{JsonFormat, pimpAny, pimpString}

import scala.reflect.runtime.universe._

class EntityMapper[T : TypeTag] {
  implicit def entityToName()(implicit tag: TypeTag[T]): String = {
    tag.tpe.typeSymbol.asClass.name.toString
  }

  def createNewDocument(implicit tag: TypeTag[T]): ODocument = {
    new ODocument(tag.tpe.typeSymbol.asClass.name.toString)
  }
}

class JsonEntityMapper[T : TypeTag : JsonFormat] extends EntityMapper[T] {
  implicit def documentToEntity(ret: ODocument): T = {
    ret.toJSON.parseJson.convertTo[T]
  }

  implicit def entityToDocument(client: T): ODocument = {
    createNewDocument.fromJSON(client.toJson.prettyPrint)
  }
}
