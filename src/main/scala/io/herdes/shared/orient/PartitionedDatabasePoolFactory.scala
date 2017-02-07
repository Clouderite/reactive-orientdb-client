package io.herdes.shared.orient

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool

import scala.collection._

class PartitionedDatabasePoolFactory {
  private val pools: mutable.Map[OrientContextCase, OPartitionedDatabasePool] = mutable.Map.empty

  def apply(context: OrientContextCase): OPartitionedDatabasePool = {
    synchronized {
      val pool = pools.getOrElse(context, newPool(context))
      pools.update(context, pool)
      pool
    }
  }

  private def newPool(context: OrientContextCase) = {
    new OPartitionedDatabasePool(connectionString(context), context.databaseLogin, context.databasePassword, context.databasePoolMin, context.databasePoolMax)
  }

  private[orient] def connectionString(implicit context: OrientContextCase) = {
    val databaseProtocol = context.databaseProtocol
    val databaseHost = context.databaseHost
    val databaseName = context.databaseName
    s"$databaseProtocol:$databaseHost/$databaseName"
  }
}

object PartitionedDatabasePoolFactory {
  val poolFactory = new PartitionedDatabasePoolFactory()
  def apply(): PartitionedDatabasePoolFactory = poolFactory
}