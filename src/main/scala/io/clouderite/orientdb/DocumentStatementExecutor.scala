package io.clouderite.orientdb

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.record.impl.ODocument
import com.orientechnologies.orient.core.sql.query.{OConcurrentResultSet, OSQLSynchQuery}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

class DocumentStatementExecutor(context: OrientContext, poolFactory: PartitionedDatabasePoolFactory) {
  private lazy val pool = poolFactory(context)

  def execute[A](statement: (ODatabaseDocumentTx) => A): A = {
    val db = pool.acquire()
    val result = Try(statement(db))
    db.close()
    
    result match {
      case Success(value) => value
      case Failure(ex) => throw ex
    }
  }

  implicit def dbToSqlDatabaseSupport(db: ODatabaseDocumentTx): DocumentSqlDatabaseSupport = new DocumentSqlDatabaseSupport(db)
}

object DocumentStatementExecutor {
  def apply(context: OrientContext): DocumentStatementExecutor = {
    apply(context, PartitionedDatabasePoolFactory())
  }

  def apply(context: OrientContext, poolFactory: PartitionedDatabasePoolFactory): DocumentStatementExecutor = {
    new DocumentStatementExecutor(context, poolFactory)
  }
}

class DocumentSqlDatabaseSupport(db: ODatabaseDocumentTx) {
  def queryBySql(sql: String): List[ODocument] = queryBySqlParams(sql)(Nil)

  def queryBySqlParams(sql: String)(params: Seq[AnyRef]): List[ODocument] = {
    val result: OConcurrentResultSet[ODocument] = db
      .command(new OSQLSynchQuery[ODocument](sql).setFetchPlan("*:-1"))
      .execute(params.toArray)
      .asInstanceOf[OConcurrentResultSet[ODocument]]
    result.asScala.toList
  }
}
