package io.herdes.shared.orient

import io.herdes.shared.orient.TestUtils.{randomInt, randomIntGtZero}
import org.scalatest.{FlatSpec, MustMatchers}

class PartitionedDatabasePoolFactoryTest extends FlatSpec with MustMatchers {
  private val testDatabaseProtocol = "remote"
  private val testDatabaseHost = "host"
  private val testDatabaseName = "name"
  private val testDatabaseLogin = "login"
  private val testDatabasePassword = "password"
  private val testDatabasePoolMin = randomIntGtZero(10)
  private val testDatabasePoolMax = testDatabasePoolMin + randomInt(10)
  private val testEntityPackageName = "entity package name"

  private def orientContext = OrientContextCase(testDatabaseProtocol, testDatabaseHost, testDatabaseName, testDatabaseLogin, testDatabasePassword, testDatabasePoolMin, testDatabasePoolMax, testEntityPackageName)

  val sut = PartitionedDatabasePoolFactory()

  "apply" should "return same pools for same context" in {
    // Given
    val createdPools = randomIntGtZero(10)

    // When
    val pools = for {
      i: Int <- (0 to createdPools).toSet
      pool = sut(orientContext)
    } yield pool

    // Then
    pools must have size 1
  }

  "apply" should "return different pools for different context" in {
    // Given
    val createdPools = randomIntGtZero(10)

    // When
    val pools = for {
      i: Int <- (1 to createdPools).toSet
      pool = sut(orientContext.copy(databaseHost = s"$testDatabaseHost-$i"))
    } yield pool

    // Then
    pools must have size createdPools
  }
}
