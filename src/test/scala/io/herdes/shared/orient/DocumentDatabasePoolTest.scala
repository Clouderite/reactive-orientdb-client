package io.herdes.shared.orient

import com.orientechnologies.orient.core.db.ODatabasePoolBase
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import io.herdes.shared.orient.AbstractDatabasePool.PoolFactory
import org.mockito.Mockito.when
import org.scalatest.{FlatSpec, MustMatchers}
import org.scalatest.mockito.MockitoSugar

import scala.util.Random

class DocumentDatabasePoolTest extends FlatSpec with MustMatchers with MockitoSugar {
  private val testDatabaseProtocol = "protocol"
  private val testDatabaseHost = "host"
  private val testDatabaseName = "name"
  private val testDatabaseLogin = "login"
  private val testDatabasePassword = "password"
  private val testDatabasePoolMin = Random.nextInt(10)
  private val testDatabasePoolMax = testDatabasePoolMin + Random.nextInt(10)

  private implicit val orientContext = mock[OrientContext]
  private implicit val poolFactory = mock[PoolFactory[ODatabaseDocumentTx]]
  private val pool = mock[ODatabasePoolBase[ODatabaseDocumentTx]]
  private val db = mock[ODatabaseDocumentTx]

  when(orientContext.databaseProtocol).thenReturn(testDatabaseProtocol)
  when(orientContext.databaseHost).thenReturn(testDatabaseHost)
  when(orientContext.databaseName).thenReturn(testDatabaseName)
  when(orientContext.databaseLogin).thenReturn(testDatabaseLogin)
  when(orientContext.databasePassword).thenReturn(testDatabasePassword)
  when(orientContext.databasePoolMin).thenReturn(testDatabasePoolMin)
  when(orientContext.databasePoolMax).thenReturn(testDatabasePoolMax)
  when(poolFactory.apply(orientContext)).thenReturn(pool)
  when(pool.acquire(s"$testDatabaseProtocol:$testDatabaseHost/$testDatabaseName", testDatabaseLogin, testDatabasePassword)).thenReturn(db)

  val sut = DocumentDatabasePool(poolFactory)

  "db" should "be acquired from pool" in {
    // Given

    // When
    val retDb = sut.db

    // Then
    retDb mustEqual db
  }
}
