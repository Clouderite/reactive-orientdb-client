package io.herdes.shared.orient

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.id.ORID
import com.orientechnologies.orient.core.record.impl.ODocument
import io.herdes.shared.orient.TestUtils.randomInt
import org.mockito.Mockito.{doReturn, spy, verify, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, MustMatchers}

class DocumentRepositoryTest extends FlatSpec with MustMatchers with MockitoSugar {
  private val testEntityName = "entity name"
  private val testEntityId = "sample id"
  private val testInvalidEntityId = "sample id invalid"
  private val testItemCollectionMinSize = 5
  private val testItemCollectionSize = randomInt(10) + testItemCollectionMinSize
  private val testDocumentsCollection = List.fill(testItemCollectionSize)(mock[ODocument])
  private val testItemsCollection = List.fill(testItemCollectionSize)(mock[Entity[String]])

  private val documentContext = mock[DocumentContext[Entity[String]]]
  private val databasePool = spy(DocumentDatabasePool())
  private val orientContext = mock[OrientContext]
  private val db = mock[ODatabaseDocumentTx]
  private val sqlDb = mock[SqlDatabaseSupport]
  private val document = mock[ODocument]
  private val item = mock[Entity[String]]
  private val te = mock[documentContext.TE]
  private val td = mock[documentContext.TD]

  private val orid = mock[ORID]
  when(documentContext.orientContext).thenReturn(orientContext)
  when(documentContext.entityName).thenReturn(testEntityName)
  when(documentContext.te).thenReturn(te)
  when(documentContext.td).thenReturn(td)
  doReturn(db).when(databasePool).db(orientContext)
  doReturn(sqlDb).when(databasePool).dbToSqlDatabaseSupport(db)

  when(te.apply(document)).thenReturn(item)
  when(td.apply(item)).thenReturn(document)
  when(document.getIdentity).thenReturn(orid)
  when(item.id).thenReturn(testEntityId)
  when(sqlDb.queryBySqlParams(s"select from $testEntityName where id=?")(testEntityId)).thenReturn(List(document))
  when(sqlDb.queryBySqlParams(s"select from $testEntityName where id=?")(testInvalidEntityId)).thenReturn(List())

  val sut = new DocumentRepository()(documentContext, databasePool)

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
    when(sqlDb.queryBySql(s"select from $testEntityName")).thenReturn(testDocumentsCollection)
    testDocumentsCollection.zip(testItemsCollection).foreach(t => when(te.apply(t._1)).thenReturn(t._2))

    // When
    val retItems = sut.findAll()

    // Then
    retItems must have size testItemCollectionSize
    retItems mustEqual testItemsCollection
  }

  "save" should "merge two documents" in {
    // Given
    val newItem = mock[Entity[String]]
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
    val newItem = mock[Entity[String]]
    val persistedItem = mock[Entity[String]]
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
