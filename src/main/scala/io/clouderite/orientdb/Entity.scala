package io.clouderite.orientdb

trait Entity[K] {
  def id: K
}
