package io.herdes.shared.orient

import com.orientechnologies.orient.core.id.ORID
import com.orientechnologies.orient.core.record.impl.ODocument

class DocumentRepository[T <: Entity[String]](implicit ec: DocumentContext[T], pool: DocumentDatabasePool = DocumentDatabasePool()) extends Repository[T] {
  import ec._
  import pool._

  def findById(id: String): T = {
    findByIdOptional(id).getOrElse(throw new ObjectNotFoundException(id))
  }

  def findByIdOptional(id: String): Option[T] = {
    findDocumentByIdOptional(id)
  }

  def findAll(): List[T] = {
    execute {
      db => db.queryBySql(s"select from $entityName")
    }
  }

  def save(item: T): T = {
    val doc: ODocument = findDocumentByIdOptional(item.id).getOrElse(createNewDocument)
    val mergedDoc = doc.merge(item, true, true)
    persist(mergedDoc)
  }

  def delete(item: T): T = {
    findDocumentByIdOptional(item.id)
      .map(doc => doc.getIdentity)
      .foreach(deleteByOrid)
    item
  }

  private def deleteByOrid(orid: ORID): Unit = {
    execute {
      db => db.delete(orid)
    }
  }

  private def findDocumentByIdOptional(id: String): Option[ODocument] = {
    execute {
      db =>
        val list = db.queryBySqlParams(s"select from $entityName where id=?")(id)
        list.headOption
    }
  }

  private def persist(doc: ODocument): T = {
    execute {
      db =>
        val persistedDoc: ODocument = db.save(doc)
        persistedDoc
    }
  }

  private implicit def documentToEntityOptional(ret: Option[ODocument]): Option[T] = {
    ret.map(document => te(document))
  }

  private implicit def documentListToEntityList(ret: List[ODocument]): List[T] = {
    ret.map(document => te(document))
  }

  private def createNewDocument = {
    new ODocument(entityName)
  }
}

object DocumentRepository {
  def apply[T <: Entity[String]](implicit ec: DocumentContext[T]): DocumentRepository[T] = {
    new DocumentRepository
  }
}
