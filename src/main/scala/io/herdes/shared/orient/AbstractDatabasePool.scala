package io.herdes.shared.orient

import com.orientechnologies.orient.core.db.{ODatabase, ODatabaseInternal, ODatabasePoolBase}

private[orient] abstract class AbstractDatabasePool[C <: ODatabase[_]] {
  implicit def db(implicit context: OrientContext): C

  private[orient] def execute[A](statement: (C) => A)(implicit context: OrientContext): A = {
    val currentDb = db
    try {
      statement(currentDb)
    } finally {
      currentDb.close()
    }
  }

  private[orient] def connectionString(implicit context: OrientContext) = {
    val databaseProtocol = context.databaseProtocol
    val databaseHost = context.databaseHost
    val databaseName = context.databaseName
    s"$databaseProtocol:$databaseHost/$databaseName"
  }
}

object AbstractDatabasePool {
  type PoolFactory[C <: ODatabaseInternal[_]] = (OrientContext) => ODatabasePoolBase[C]
}