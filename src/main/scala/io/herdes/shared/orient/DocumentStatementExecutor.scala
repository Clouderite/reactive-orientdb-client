package io.herdes.shared.orient

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.record.impl.ODocument
import com.orientechnologies.orient.core.sql.query.{OConcurrentResultSet, OSQLSynchQuery}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

class DocumentStatementExecutor(context: OrientContext, poolFactory: PartitionedDatabasePoolFactory) {
  private lazy val pool = poolFactory(context)

  def execute[A](statement: (ODatabaseDocumentTx) => A): A = {
    val db = getDb
    val result = Try(statement(db))
    db.close()
    
    result match {
      case Success(value) => value
      case Failure(ex) => throw ex
    }
  }

  private def getDb[A] = {
    pool.acquire()
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
  def queryBySql(sql: String): List[ODocument] = queryBySqlParams(sql)(Array.empty)

  def queryBySqlParams(sql: String)(params: AnyRef*): List[ODocument] = {
    val result: OConcurrentResultSet[ODocument] = db
      .command(new OSQLSynchQuery[ODocument](sql))
      .execute(params.asJava.toArray)
      .asInstanceOf[OConcurrentResultSet[ODocument]]
    result.asScala.toList
  }
}
