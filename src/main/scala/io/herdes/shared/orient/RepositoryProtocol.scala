package io.herdes.shared.orient

abstract sealed class RepositoryProtocol
case class GetItem(id: String) extends RepositoryProtocol
case class ListItems() extends RepositoryProtocol
case class SaveItem[T <: Entity[String]](item: T) extends RepositoryProtocol
case class DeleteItem(id: String) extends RepositoryProtocol

