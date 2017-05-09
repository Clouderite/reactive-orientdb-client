package io.clouderite.orientdb

class QueriedObjectNotFoundException(query: String, params: Seq[AnyRef]) extends RuntimeException {
  override def getMessage: String = {
    s"there is no object with query '$query' and params '$params"
  }
}
