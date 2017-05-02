package io.clouderite.orientdb

class ObjectNotFoundException(id: String) extends RuntimeException {
  override def getMessage: String = {
    s"there is no object with id '$id'"
  }
}
