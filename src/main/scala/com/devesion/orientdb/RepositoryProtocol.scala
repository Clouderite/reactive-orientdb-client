package com.devesion.orientdb

abstract sealed class RepositoryProtocol
case class GetItem(id: String) extends RepositoryProtocol
case class ListItems() extends RepositoryProtocol
case class QueryItems(where: String) extends RepositoryProtocol
case class SaveItem[T <: Entity[String]](item: T) extends RepositoryProtocol
case class MergeItem[T <: Entity[String]](item: T) extends RepositoryProtocol
case class DeleteItem(id: String) extends RepositoryProtocol

