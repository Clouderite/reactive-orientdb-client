package com.devesion.orientdb

class ObjectRepository[T <: Entity[String]]()(implicit oc: ObjectContext[T], executor: ObjectStatementExecutor[T]) extends Repository[T] {

  def findById(id: String): T = {
    findByIdOptional(id).getOrElse(throw new ObjectNotFoundException(id))
  }

  def findByIdOptional(id: String): Option[T] = {
    import executor.dbToSqlDatabaseSupport
    val entityName = oc.entityName
    executor.execute {
      db =>
        db.queryBySqlParams(s"select from $entityName where id=?")(id).flatMap(entity => db.detach(entity)).headOption
    }
  }

  def findAll(): List[T] = {
    import executor.dbToSqlDatabaseSupport
    val entityName = oc.entityName
    executor.execute {
      db =>
        db.queryBySql(s"select from $entityName").flatMap(entity => db.detach(entity))
    }
  }

  def save(item: T): T = {
    executor.execute {
      db =>
        db.save(item)
        db.detach(item)
    }
  }

  def delete(item: T): T = {
    executor.execute {
      db =>
        db.delete(item)
        item
    }
  }
}

object ObjectRepository {
  def apply[T <: Entity[String]](implicit oc: ObjectContext[T]): ObjectRepository[T] = {
    new ObjectRepository()(oc, ObjectStatementExecutor[T]()(oc.orientContext))
  }
}
