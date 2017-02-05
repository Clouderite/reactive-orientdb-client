package io.herdes.shared.orient

import com.orientechnologies.orient.`object`.db.{OObjectDatabasePool, OObjectDatabaseTx}

private[orient] object ObjectDatabasePool extends AbstractDatabasePool[OObjectDatabaseTx] {

  override implicit def db(implicit context: OrientContext): OObjectDatabaseTx = {
    import context._
    val pool = OObjectDatabasePool.global(databasePoolMin, databasePoolMax)
    val db = pool.acquire(connectionString(context), databaseLogin, databasePassword)
    db.getEntityManager.registerEntityClasses(entityPackageName)
    db
  }
}
