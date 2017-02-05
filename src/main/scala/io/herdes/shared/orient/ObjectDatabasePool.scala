package io.herdes.shared.orient

import com.orientechnologies.orient.`object`.db.{OObjectDatabasePool, OObjectDatabaseTx}

private[orient] object ObjectDatabasePool extends AbstractDatabasePool[OObjectDatabaseTx] {

  override implicit def db(implicit context: OrientContext): OObjectDatabaseTx = {
    val pool = OObjectDatabasePool.global(context.databasePoolMin, context.databasePoolMax)
    val db = pool.acquire(connectionString(context), context.databaseLogin, context.databasePassword)
    db.getEntityManager.registerEntityClasses(context.entityPackageName)
    db
  }
}
