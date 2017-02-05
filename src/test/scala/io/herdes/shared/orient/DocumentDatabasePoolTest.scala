package io.herdes.shared.orient

import com.orientechnologies.orient.core.db.ODatabasePoolBase
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import io.herdes.shared.orient.AbstractDatabasePool.PoolFactory
import org.mockito.Mockito.when
import org.scalatest.{FlatSpec, MustMatchers}
import org.scalatest.mockito.MockitoSugar

import scala.util.Random

class DocumentDatabasePoolTest extends FlatSpec with MustMatchers with MockitoSugar {
  private val TestDatabaseProtocol = "protocol"
  private val TestDatabaseHost = "host"
  private val TestDatabaseName = "name"
  private val TestDatabaseLogin = "login"
  private val TestDatabasePassword = "password"
  private val TestDatabasePoolMin = Random.nextInt(10)
  private val TestDatabasePoolMax = TestDatabasePoolMin + Random.nextInt(10)

  private implicit val orientContext = mock[OrientContext]
  private implicit val poolFactory = mock[PoolFactory[ODatabaseDocumentTx]]
  private val pool = mock[ODatabasePoolBase[ODatabaseDocumentTx]]
  private val db = mock[ODatabaseDocumentTx]

  when(orientContext.databaseProtocol).thenReturn(TestDatabaseProtocol)
  when(orientContext.databaseHost).thenReturn(TestDatabaseHost)
  when(orientContext.databaseName).thenReturn(TestDatabaseName)
  when(orientContext.databaseLogin).thenReturn(TestDatabaseLogin)
  when(orientContext.databasePassword).thenReturn(TestDatabasePassword)
  when(orientContext.databasePoolMin).thenReturn(TestDatabasePoolMin)
  when(orientContext.databasePoolMax).thenReturn(TestDatabasePoolMax)
  when(poolFactory.apply(orientContext)).thenReturn(pool)
  when(pool.acquire(s"$TestDatabaseProtocol:$TestDatabaseHost/$TestDatabaseName", TestDatabaseLogin, TestDatabasePassword)).thenReturn(db)

  val sut = DocumentDatabasePool(poolFactory)

  "db" should "be acquired from pool" in {
    // Given

    // When
    val retDb = sut.db

    // Then
    retDb mustEqual db
  }
}
