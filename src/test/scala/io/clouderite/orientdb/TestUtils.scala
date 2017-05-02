package io.clouderite.orientdb

import java.security.SecureRandom

object TestUtils {
  private val secureRandom = new SecureRandom

  def randomIntGtZero(i: Int): Int = {
    randomInt(i) + 1
  }
  
  def randomInt(i: Int): Int = {
    secureRandom.nextInt(i)
  }
}
