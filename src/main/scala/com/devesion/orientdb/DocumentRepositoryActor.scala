package com.devesion.orientdb

class DocumentRepositoryActor[T <: Entity[String]](implicit val dc: DocumentContext[T]) extends AbstractRepositoryActor(DocumentRepository(dc))
