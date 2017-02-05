package io.herdes.shared.orient

import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery

import scala.collection.JavaConverters._

class ObjectRepository[T <: Entity[String]](implicit oc: ObjectContext[T], pool: AbstractDatabasePool[OObjectDatabaseTx] = ObjectDatabasePool) extends Repository[T] {
  import oc._
  import pool._
  
  def findById(id: String): T = {
    findByIdOptional(id).getOrElse(throw new ObjectNotFoundException(id))
  }

  def findByIdOptional(id: String): Option[T] = {
    execute {
      db =>
        db.queryBySqlParams(s"select from $entityName where id=?")(id).flatMap(entity => db.detach(entity)).headOption
    }
  }

  def findAll(): List[T] = {
    execute {
      db =>
        db.queryBySql(s"select from $entityName").flatMap(entity => db.detach(entity))
    }
  }

  def save(item: T): T = {
    execute {
      db =>
        db.save(item)
        db.detach(item)
    }
  }

  def delete(item: T): T = {
    execute {
      db =>
        db.delete(item)
        item
    }
  }

  implicit private def dbToSqlDatabaseSupport(db: OObjectDatabaseTx): SqlDatabaseSupport = {
    new SqlDatabaseSupport(db)
  }

  class SqlDatabaseSupport(db: OObjectDatabaseTx) {
    def queryBySql(sql: String): List[T] = {
      queryBySqlParams(sql)()
    }

    def queryBySqlParams(sql: String)(params: AnyRef*): List[T] = {
      val results: java.util.List[T] = db.query(new OSQLSynchQuery[T](sql), params.toArray: _*)
      results.asScala.toList
    }
  }
}

object ObjectRepository {
  def apply[T <: Entity[String]](implicit oc: ObjectContext[T]): ObjectRepository[T] = {
    new ObjectRepository
  }
}
