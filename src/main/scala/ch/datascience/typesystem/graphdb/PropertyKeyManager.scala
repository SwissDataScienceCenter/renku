package ch.datascience.typesystem.graphdb

import ch.datascience.typesystem.model.DataType
import com.sun.net.httpserver.Authenticator.Failure
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.schema.JanusGraphManagement
import org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality

/**
  * Created by johann on 21/03/17.
  */
trait PropertyKeyManager {

  def getPropertyKey(name: String): GraphManagementAction[PropertyKey] = GraphManagementAction { mgmt: JanusGraphManagement =>
    val pk = mgmt.getPropertyKey(name)
    pk match {
      case null => throw new IllegalArgumentException(s"Property key '$name' does not exists")
      case _ => pk
    }
  }

  def addPropertyKey(name: String, dataType: DataType, cardinality: Cardinality): GraphManagementAction[PropertyKey] = GraphManagementAction { mgmt: JanusGraphManagement =>
    mgmt.containsPropertyKey(name) match {
      case true =>
        throw new IllegalArgumentException(s"Property key '$name' already exists")
      case false =>
        val maker = mgmt.makePropertyKey(name)
        maker.dataType(dataType.javaClass())
        val janusCardinality = org.janusgraph.core.Cardinality.convert(cardinality)
        maker.cardinality(janusCardinality)
        val pk = maker.make()
        pk
    }
  }

}
