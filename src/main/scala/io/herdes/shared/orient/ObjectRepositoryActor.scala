package io.herdes.shared.orient

class ObjectRepositoryActor[T <: Entity[String]](implicit val oc: ObjectContext[T]) extends AbstractRepositoryActor(ObjectRepository(oc))
