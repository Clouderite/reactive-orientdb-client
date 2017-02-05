package io.herdes.shared.orient

trait Repository[T <: Entity[String]] {
  def findById(id: String): T
  def findByIdOptional(id: String): Option[T]
  def findAll(): List[T]
  def save(item: T): T
  def delete(item: T): T
}
