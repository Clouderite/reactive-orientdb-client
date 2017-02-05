package io.herdes.shared.orient

import java.security.SecureRandom

object TestUtils {
  private val secureRandom = new SecureRandom

  def randomInt(i: Int): Int = {
    secureRandom.nextInt(i)
  }
}
