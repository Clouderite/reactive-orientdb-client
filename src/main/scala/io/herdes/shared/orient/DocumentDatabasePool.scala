package io.herdes.shared.orient

import com.orientechnologies.orient.core.db.ODatabasePoolBase
import com.orientechnologies.orient.core.db.document.{ODatabaseDocumentPool, ODatabaseDocumentTx}
import com.orientechnologies.orient.core.record.impl.ODocument
import com.orientechnologies.orient.core.sql.query.{OConcurrentResultSet, OSQLSynchQuery}
import io.herdes.shared.orient.AbstractDatabasePool.PoolFactory
import io.herdes.shared.orient.DocumentDatabasePoolFactory.poolFactory

import scala.collection.JavaConverters._

class DocumentDatabasePool(implicit poolFactory: PoolFactory[ODatabaseDocumentTx]) extends AbstractDatabasePool[ODatabaseDocumentTx] {
  override implicit def db(implicit context: OrientContext): ODatabaseDocumentTx = {
    import context._
    val pool = poolFactory(context)
    pool.acquire(connectionString(context), databaseLogin, databasePassword)
  }

  implicit def dbToSqlDatabaseSupport(db: ODatabaseDocumentTx): SqlDatabaseSupport = new SqlDatabaseSupport(db)
}

class SqlDatabaseSupport(db: ODatabaseDocumentTx) {
  def queryBySql(sql: String): List[ODocument] = queryBySqlParams(sql)(Array.empty)

  def queryBySqlParams(sql: String)(params: AnyRef*): List[ODocument] = {
    val result: OConcurrentResultSet[ODocument] = db
      .command(new OSQLSynchQuery[ODocument](sql))
      .execute(params.asJava.toArray)
      .asInstanceOf[OConcurrentResultSet[ODocument]]
    result.asScala.toList
  }
}

object DocumentDatabasePool {
  def apply(implicit poolFactory: PoolFactory[ODatabaseDocumentTx] = poolFactory): DocumentDatabasePool = new DocumentDatabasePool()
}

object DocumentDatabasePoolFactory {
  def poolFactory(orientContext: OrientContext): ODatabasePoolBase[ODatabaseDocumentTx] = {
    import orientContext._
    ODatabaseDocumentPool.global(databasePoolMin, databasePoolMax)
  }
}