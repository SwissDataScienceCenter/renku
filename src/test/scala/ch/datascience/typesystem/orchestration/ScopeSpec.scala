package ch.datascience.typesystem.orchestration

import ch.datascience.typesystem.{AsyncUnitSpec, Cardinality, DataType, DatabaseSetup}
import org.scalatest.BeforeAndAfterEach

/**
  * Created by johann on 24/04/17.
  */
class ScopeSpec extends AsyncUnitSpec with OrchestrationSetup with BeforeAndAfterEach {

  import profile.api._

  behavior of "Scope"

  it should "be there, empty" in {
    ol.getCurrentScope map { scope =>
      scope.propertyDefinitions shouldBe empty
      scope.namedRecordTypes shouldBe empty
    }
  }

  it should "not have any property" in {
    ol.scopeForPropertyKey("foo", "bar") map { scope =>
      scope.propertyDefinitions shouldBe empty
      scope.namedRecordTypes shouldBe empty
    }
  }

  it should "allow to add properties" in {
    val create = ol.graphDomains.createGraphDomain("foo") flatMap { _ => println("create foo!"); ol.propertyKeys.createPropertyKey("foo", "bar", DataType.Integer, Cardinality.List) }
    val fetch = create flatMap { _ => println("create!"); ol.scopeForPropertyKey("foo", "bar") }
    fetch map { scope =>
      scope.propertyDefinitions shouldBe empty
      scope.namedRecordTypes shouldBe empty
    }
  }

}
