package io.clouderite.orientdb

import com.orientechnologies.orient.core.record.impl.ODocument

trait Repository[T <: Entity[String]] {
  def findById(id: String): T
  def findByIdOptional(id: String): Option[T]
  def findAll(): List[T]
  def findDocumentById(id: String): ODocument
  def findDocumentByIdOptional(id: String): Option[ODocument]
  def query(where: String, params: Seq[AnyRef]): List[T]
  def save(item: T): T
  def merge(item: T): T
  def delete(item: T): T
}
