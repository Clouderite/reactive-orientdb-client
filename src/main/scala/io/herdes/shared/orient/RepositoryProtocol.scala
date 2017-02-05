package io.herdes.shared.orient

abstract sealed class RepositoryProtocol
case class GetItem[T <: Entity[String]](id: String) extends RepositoryProtocol
case class ListItems[T <: Entity[String]]() extends RepositoryProtocol
case class SaveItem[T <: Entity[String]](item: T) extends RepositoryProtocol
case class DeleteItem[T <: Entity[String]](id: String) extends RepositoryProtocol

