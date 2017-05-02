package io.clouderite.orientdb

class ObjectRepositoryActor[T <: Entity[String]](implicit val oc: ObjectContext[T]) extends AbstractRepositoryActor(ObjectRepository(oc))
