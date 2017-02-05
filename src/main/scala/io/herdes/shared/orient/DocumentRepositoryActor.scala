package io.herdes.shared.orient

class DocumentRepositoryActor[T <: Entity[String]](implicit val dc: DocumentContext[T]) extends AbstractRepositoryActor(DocumentRepository(dc))
