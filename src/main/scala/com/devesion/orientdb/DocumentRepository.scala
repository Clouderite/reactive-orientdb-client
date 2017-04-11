package com.devesion.orientdb

import com.orientechnologies.orient.core.id.ORID
import com.orientechnologies.orient.core.record.impl.ODocument

class DocumentRepository[T <: Entity[String]](implicit ec: DocumentContext[T], executor: DocumentStatementExecutor) extends Repository[T] {
  import ec._

  def findById(id: String): T = {
    findByIdOptional(id).getOrElse(throw new ObjectNotFoundException(id))
  }

  def findByIdOptional(id: String): Option[T] = {
    findDocumentByIdOptional(id)
  }

  def findAll(): List[T] = {
    import executor.dbToSqlDatabaseSupport
    executor.execute {
      db => db.queryBySql(s"select from $entityName")
    }
  }

  def query(where: String): List[T] = {
    import executor.dbToSqlDatabaseSupport
    executor.execute {
      db =>
         db.queryBySql(s"select from $entityName $where")
    }
  }

  def save(item: T): T = {
    val doc: ODocument = findDocumentByIdOptional(item.id).getOrElse(createNewDocument)
    doc.clear()
    val mergedDoc = doc.merge(item, true, true)
    persist(mergedDoc)
  }

  def merge(item: T): T = {
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
    executor.execute {
      db => db.delete(orid)
    }
  }

  private def findDocumentByIdOptional(id: String): Option[ODocument] = {
    import executor.dbToSqlDatabaseSupport
    executor.execute {
      db =>
        val list = db.queryBySqlParams(s"select from $entityName where id=?")(id)
        list.headOption
    }
  }

  private def persist(doc: ODocument): T = {
    executor.execute {
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
  def apply[T <: Entity[String]](implicit dc: DocumentContext[T]): DocumentRepository[T] = {
    apply(dc, DocumentStatementExecutor(dc.orientContext))
  }
  
  def apply[T <: Entity[String]](implicit dc: DocumentContext[T], poolFactory: PartitionedDatabasePoolFactory): DocumentRepository[T] = {
    apply(dc, DocumentStatementExecutor(dc.orientContext, poolFactory))
  }

  def apply[T <: Entity[String]](implicit dc: DocumentContext[T], executor: DocumentStatementExecutor): DocumentRepository[T] = {
    new DocumentRepository()(dc, executor)
  }
}
