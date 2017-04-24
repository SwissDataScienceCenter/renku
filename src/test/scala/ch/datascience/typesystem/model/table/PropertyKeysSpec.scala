package ch.datascience.typesystem.model.table

import java.util.UUID

import ch.datascience.typesystem.relationaldb.row.{GraphDomain, PropertyKey}
import ch.datascience.typesystem.{AsyncUnitSpec, Cardinality, DataType, DatabaseSetup}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import ch.datascience.typesystem.model.{PropertyKey => PropertyKeyModel}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by johann on 13/04/17.
  */
class PropertyKeysSpec extends AsyncUnitSpec with DatabaseSetup with BeforeAndAfterAll with BeforeAndAfterEach {

  import profile.api._

  val graphDomain = GraphDomain(UUID.randomUUID(), "foo")

  behavior of "PropertyKeys table"

  it should "be empty" in {
    val f = db.run(dal.propertyKeys.result)
    f map { list => list shouldBe empty }
  }

  it should "allow to add a property key" in {
    val propertyKey = PropertyKey(UUID.randomUUID(), graphDomain.id, "bar")
    val f = db.run(dal.propertyKeys.add(propertyKey))
    f map { count => count shouldBe 1 }
  }

  it should "allow to add a property key and get it back" in {
    val propertyKey = PropertyKey(UUID.randomUUID(), graphDomain.id, "bar", DataType.Double, Cardinality.Set)
    val insert: DBIO[Int] = dal.propertyKeys.add(propertyKey)
    val select: DBIO[Option[PropertyKey]] = dal.propertyKeys.findByNamespaceAndName("foo", "bar").result.headOption
    val f = db.run(insert andThen select)
    f map { opt => opt shouldBe Some(propertyKey) }
  }

  it should "allow to add a property key and get it back (2)" in {
    val propertyKey = PropertyKey(UUID.randomUUID(), graphDomain.id, "bar", DataType.Double, Cardinality.List)
    val insert: DBIO[Int] = dal.propertyKeys.add(propertyKey)
    val select: DBIO[Option[PropertyKeyModel]] = dal.propertyKeys.findByNamespaceAndNameAsModel("foo", "bar").result.headOption
    val f = db.run(insert andThen select)
    f map { opt =>
      opt shouldBe Some(PropertyKeyModel("foo", "bar", DataType.Double, Cardinality.List))
    }
  }

  it should "allow to add a property keys properly" in {
    val graphDomain2 = GraphDomain(UUID.randomUUID(), "hello")
    val propertyKey1 = PropertyKey(UUID.randomUUID(), graphDomain.id, "bar")
    val propertyKey2 = PropertyKey(UUID.randomUUID(), graphDomain2.id, "baz")
    val insert: DBIO[Int] = dal.graphDomains.add(graphDomain2) andThen dal.propertyKeys.add(propertyKey1) andThen dal.propertyKeys.add(propertyKey2)
    val select: DBIO[Seq[PropertyKeyModel]] = dal.propertyKeys.withGraphDomainAsModel.result
    val f = db.run(insert andThen select)
    f map { seq =>
      seq should contain (PropertyKeyModel("foo", "bar"))
      seq should contain (PropertyKeyModel("hello", "baz"))
      seq shouldNot contain (PropertyKeyModel("hello", "bar"))
      seq shouldNot contain (PropertyKeyModel("foo", "baz"))
    }
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    val f = db.run(dal.graphDomains.add(graphDomain))
    Await.result(f, Duration.Inf)
  }

//  it should "allow to add a graph domain and get it back" in {
//    val graphDomain = GraphDomain(UUID.randomUUID(), "foo")
//    val insert: DBIO[Int] = dal.graphDomains.add(graphDomain)
//    val select: DBIO[Option[GraphDomain]] = dal.graphDomains.findByNamespace("foo").result.headOption
//    val f = db.run(insert andThen select)
//    f map { opt => opt shouldBe Some(graphDomain) }
//  }

  override protected def afterEach(): Unit = {
    try super.afterEach()
    finally {
      val dropAll: DBIO[Int] = dal.propertyKeys.delete
      val run = db.run(dropAll)
      Await.result(run, Duration.Inf)
    }
  }

}
