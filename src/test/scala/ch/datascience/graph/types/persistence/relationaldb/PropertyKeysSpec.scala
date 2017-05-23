package ch.datascience.graph.types.persistence.relationaldb

import java.util.UUID

import ch.datascience.graph.types.persistence.model.{GraphDomain, RichPropertyKey}
import ch.datascience.graph.types.persistence.{AsyncUnitSpec, DatabaseSetup}
import ch.datascience.graph.types.{Cardinality, DataType}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by johann on 13/04/17.
  */
class PropertyKeysSpec extends AsyncUnitSpec with DatabaseSetup with BeforeAndAfterAll with BeforeAndAfterEach {

  import profile.api._
  import dal._

  val graphDomain = GraphDomain(UUID.randomUUID(), "foo")

  behavior of "PropertyKeys table"

  it should "be empty" in {
    val f = db.run(dal.propertyKeys.result)
    f map { list => list shouldBe empty }
  }

  it should "allow to add a property key" in {
    val propertyKey = RichPropertyKey(UUID.randomUUID(), graphDomain, "bar", DataType.String, Cardinality.Single)
    val f = db.run( dal.propertyKeys add propertyKey )
    f map { unit => unit shouldBe () }
  }

  it should "allow to add a property key and get it back" in {
    val propertyKey = RichPropertyKey(UUID.randomUUID(), graphDomain, "bar", DataType.Double, Cardinality.Set)
    val insert: DBIO[Unit] = dal.propertyKeys add propertyKey
    val select: DBIO[Option[RichPropertyKey]] = dal.propertyKeys.findByNamespaceAndName("foo", "bar").result.headOption
    val f = db.run(insert andThen select)
    f map { opt => opt shouldBe Some(propertyKey) }
  }

  it should "allow to add a property keys properly" in {
    val graphDomain2 = GraphDomain(UUID.randomUUID(), "hello")
    val propertyKey1 = RichPropertyKey(UUID.randomUUID(), graphDomain, "bar", DataType.Double, Cardinality.List)
    val propertyKey2 = RichPropertyKey(UUID.randomUUID(), graphDomain2, "baz", DataType.String, Cardinality.Single)
    val insert: DBIO[Unit] = dal.graphDomains.add(graphDomain2) andThen dal.propertyKeys.add(propertyKey1) andThen dal.propertyKeys.add(propertyKey2)
    val select: DBIO[Seq[RichPropertyKey]] = dal.propertyKeys.mapped.result
    val f = db.run(insert andThen select)
    f map { seq =>
      seq should contain (RichPropertyKey(propertyKey1.id, graphDomain, "bar", DataType.Double, Cardinality.List))
      seq should contain (RichPropertyKey(propertyKey2.id, graphDomain2, "baz", DataType.String, Cardinality.Single))
      seq shouldNot contain (RichPropertyKey(propertyKey1.id, graphDomain2, "baz", DataType.String, Cardinality.Single))
      seq shouldNot contain (RichPropertyKey(propertyKey2.id, graphDomain, "bar", DataType.Double, Cardinality.List))
    }
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    val f = db.run( dal.graphDomains add graphDomain )
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
