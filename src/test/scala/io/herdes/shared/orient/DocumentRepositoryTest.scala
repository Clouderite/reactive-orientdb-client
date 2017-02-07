package io.herdes.shared.orient

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.id.ORID
import com.orientechnologies.orient.core.record.impl.ODocument
import io.herdes.shared.orient.DocumentContext.{TD, TE}
import io.herdes.shared.orient.TestUtils.randomInt
import org.mockito.Mockito
import org.mockito.Mockito.{verify, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, MustMatchers}

class DocumentRepositoryTest extends FlatSpec with MustMatchers with MockitoSugar {
  type TT = Entity[String]
  
  private val testEntityName = "entity name"
  private val testEntityId = "sample id"
  private val testInvalidEntityId = "sample id invalid"
  private val testItemCollectionMinSize = 5
  private val testItemCollectionSize = randomInt(10) + testItemCollectionMinSize
  private val testDocumentsCollection = List.fill(testItemCollectionSize)(mock[ODocument])
  private val testItemsCollection = List.fill(testItemCollectionSize)(mock[TT])

  private val documentContext = mock[DocumentContext[TT]]
  private val poolFactory = mock[PartitionedDatabasePoolFactory]
  private val pool = mock[OPartitionedDatabasePool]
  private val orientContext = mock[OrientContext]
  private val executor = Mockito.spy(new DocumentStatementExecutor(orientContext, poolFactory))
  private val db = mock[ODatabaseDocumentTx]
  private val dbSql = mock[DocumentSqlDatabaseSupport]
  private val document = mock[ODocument]
  private val item = mock[TT]
  private val te = mock[TE[TT]]
  private val td = mock[TD[TT]]
  private val orid = mock[ORID]

  when(documentContext.orientContext).thenReturn(orientContext)
  when(documentContext.entityName).thenReturn(testEntityName)
  when(documentContext.te).thenReturn(te)
  when(documentContext.td).thenReturn(td)
  when(poolFactory(orientContext)).thenReturn(pool)
  when(pool.acquire()).thenReturn(db)
  when(executor.dbToSqlDatabaseSupport(db)).thenReturn(dbSql)

  when(te.apply(document)).thenReturn(item)
  when(td.apply(item)).thenReturn(document)
  when(document.getIdentity).thenReturn(orid)
  when(item.id).thenReturn(testEntityId)
  when(dbSql.queryBySqlParams(s"select from $testEntityName where id=?")(testEntityId)).thenReturn(List(document))
  when(dbSql.queryBySqlParams(s"select from $testEntityName where id=?")(testInvalidEntityId)).thenReturn(List())

  val sut = DocumentRepository(documentContext, executor)

  "find by id" should "find and return existing item" in {
    // Given

    // When
    val retItem = sut.findById(testEntityId)

    // Then
    retItem mustBe item
  }

  "find by id" should "should throw an exception for non-existing item" in {
    // Given

    // When
    assertThrows[ObjectNotFoundException] {
      sut.findById(testInvalidEntityId)
    }
  }

  "find by id optional" should "find item and return Some" in {
    // Given

    // When
    val retItem = sut.findByIdOptional(testEntityId)

    // Then
    retItem mustBe Some(item)
  }

  "find by id optional" should "shouldn't find item and should return None" in {
    // Given

    // When
    val retItem = sut.findByIdOptional(testInvalidEntityId)

    // Then
    retItem mustBe None
  }

  "find all" should "find and return all items" in {
    // Given
    when(dbSql.queryBySql(s"select from $testEntityName")).thenReturn(testDocumentsCollection)
    testDocumentsCollection.zip(testItemsCollection).foreach(t => when(te.apply(t._1)).thenReturn(t._2))

    // When
    val retItems = sut.findAll()

    // Then
    retItems must have size testItemCollectionSize
    retItems mustEqual testItemsCollection
  }

  "save" should "merge two documents" in {
    // Given
    val newItem = mock[TT]
    val newDocument = mock[ODocument]
    when(newItem.id).thenReturn(testEntityId)
    when(td.apply(newItem)).thenReturn(newDocument)

    // When
    sut.save(newItem)

    // Then
    verify(document).merge(newDocument, true, true)
  }

  "save" should "return merged document" in {
    // Given
    val newItem = mock[TT]
    val persistedItem = mock[TT]
    val newDocument = mock[ODocument]
    val mergedDocument = mock[ODocument]
    val persistedDocument = mock[ODocument]
    when(newItem.id).thenReturn(testEntityId)
    when(td.apply(newItem)).thenReturn(newDocument)
    when(document.merge(newDocument, true, true)).thenReturn(mergedDocument)
    when(db.save(mergedDocument)).thenReturn(persistedDocument)
    when(te.apply(persistedDocument)).thenReturn(persistedItem)

    // When
    val retItem = sut.save(newItem)

    // Then
    retItem mustEqual persistedItem
  }

  "delete" should "get rid of item with given id" in {
    // Given

    // When
    sut.delete(item)

    // Then
    verify(db).delete(orid)
  }
}
