package com.devesion.orientdb

import scala.reflect.runtime.universe._

class DocumentRepositoryActor[T <: Entity[String] : TypeTag](implicit val dc: DocumentContext[T]) extends AbstractRepositoryActor(DocumentRepository(dc))
