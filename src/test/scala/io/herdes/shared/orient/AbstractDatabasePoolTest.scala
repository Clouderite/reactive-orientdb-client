package io.herdes.shared.orient

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import org.mockito.Mockito
import org.mockito.Mockito.{spy, verify, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, MustMatchers}

class AbstractDatabasePoolTest extends FlatSpec with MustMatchers with MockitoSugar {
  private val TestStatementReturn = "return value"
  private implicit val orientContext = mock[OrientContext]
  private val db = mock[ODatabaseDocumentTx]
  private val statement: (ODatabaseDocumentTx) => String = mock[(ODatabaseDocumentTx) => String]
  when(statement(db)).thenReturn(TestStatementReturn)

  private val sut = spy(classOf[AbstractDatabasePool[ODatabaseDocumentTx]])
  Mockito.doReturn(db).when(sut).db

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
    retValue mustEqual TestStatementReturn
  }
}
