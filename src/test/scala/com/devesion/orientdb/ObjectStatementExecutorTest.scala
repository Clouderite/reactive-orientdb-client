package com.devesion.orientdb

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import org.mockito.Mockito.{verify, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, MustMatchers}

class ObjectStatementExecutorTest extends FlatSpec with MustMatchers with MockitoSugar {
  private val testStatementReturn = "return value"
  private val db = mock[ODatabaseDocumentTx]
  private val statement: (ODatabaseDocumentTx) => String = mock[(ODatabaseDocumentTx) => String]
  private val poolFactory = mock[PartitionedDatabasePoolFactory]
  private val pool = mock[OPartitionedDatabasePool]
  private val orientContext = mock[OrientContext]

  when(poolFactory(orientContext)).thenReturn(pool)
  when(pool.acquire()).thenReturn(db)
  when(statement(db)).thenReturn(testStatementReturn)

  private val sut = ObjectStatementExecutor(orientContext, poolFactory)

  "execute" should "execute statement" in {
    // Given

    // When
    sut.execute(statement)

    // Then
    verify(statement).apply(db)
  }

  "execute" should "return statement value" in {
    // Given

    // When
    val retValue: String = sut.execute(statement)

    // Then
    retValue mustEqual testStatementReturn
  }
}
