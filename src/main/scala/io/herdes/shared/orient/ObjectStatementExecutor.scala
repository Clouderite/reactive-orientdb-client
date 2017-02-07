package io.herdes.shared.orient

import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery

import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.util.{Failure, Success, Try}

class ObjectStatementExecutor[T](context: OrientContext, poolFactory: PartitionedDatabasePoolFactory) {
  private val pool = poolFactory(context)

  def execute[A](statement: (OObjectDatabaseTx) => A)(implicit context: ObjectContext[T]): A = {
    val result = Try(statement(getDb(context)))

    result match {
      case Success(value) => value
      case Failure(ex) => throw ex
    }
  }

  private def getDb[A](context: ObjectContext[T]) = {
    implicit val orientContext: OrientContext = context.orientContext
    val db: OObjectDatabaseTx = pool.acquire()
    db.getEntityManager.registerEntityClasses(orientContext.entityPackageName)
    db
  }

  implicit def dbToObjectDb(db: ODatabaseDocumentTx): OObjectDatabaseTx = new OObjectDatabaseTx(db)
  implicit def dbToSqlDatabaseSupport(db: OObjectDatabaseTx): ObjectSqlDatabaseSupport[T] = new ObjectSqlDatabaseSupport[T](db)
}

object ObjectStatementExecutor {
  def apply[T]()(implicit context: OrientContext): ObjectStatementExecutor[T] = {
    new ObjectStatementExecutor[T](context, PartitionedDatabasePoolFactory())
  }

  def apply(context: OrientContext, poolFactory: PartitionedDatabasePoolFactory): DocumentStatementExecutor = {
    new DocumentStatementExecutor(context, poolFactory)
  }
}

class ObjectSqlDatabaseSupport[T](db: OObjectDatabaseTx) {
  def queryBySql(sql: String): List[T] = {
    queryBySqlParams(sql)()
  }

  def queryBySqlParams(sql: String)(params: AnyRef*): List[T] = {
    val results: java.util.List[T] = db.query(new OSQLSynchQuery[T](sql), params.toArray: _*)
    results.asScala.toList
  }
}
