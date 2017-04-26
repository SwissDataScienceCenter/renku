package ch.datascience.typesystem.orchestration

import ch.datascience.typesystem.{AsyncUnitSpec, DatabaseSetup}
import org.scalatest.BeforeAndAfterEach
import ch.datascience.typesystem.model.{Cardinality, DataType, PropertyKey}

import scala.concurrent.Future

/**
  * Created by johann on 24/04/17.
  */
class ScopeSpec extends AsyncUnitSpec with OrchestrationSetup with BeforeAndAfterEach {

  import profile.api._

  behavior of "Scope"

  it should "be there, empty" in {
    ol.getCurrentValidator map { validator =>
      validator.propertyKeys shouldBe empty
      validator.namedRecordTypes shouldBe empty
    }
  }

  it should "not have any property" in {
    ol.getValidatorForPropertyKey("foo", "bar") map { validator =>
      validator.propertyKeys shouldBe empty
      validator.namedRecordTypes shouldBe empty
    }
  }

  it should "allow to add properties" in {
    val create = ol.graphDomains.createGraphDomain("foo") flatMap { _ => ol.propertyKeys.createPropertyKey("foo", "bar", DataType.Integer, Cardinality.List) }
    val fetch = create flatMap { _ => ol.getValidatorForPropertyKey("foo", "bar") }
    fetch map { validator =>
      validator.propertyKeys should contain key "foo:bar"
      validator.namedRecordTypes shouldBe empty
    }
  }

}
