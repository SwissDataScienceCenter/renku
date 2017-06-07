package ch.datascience.graph.types.persistence.graphdb

import ch.datascience.graph.types.Multiplicity
import org.janusgraph.core.EdgeLabel
import org.janusgraph.core.schema.JanusGraphManagement

/**
  * Created by johann on 07/06/17.
  */
trait EdgeLabelComponent {

  object edgeLabels {

    def getEdgeLabel(name: String): GraphManagementAction[EdgeLabel] = GraphManagementAction { mgmt: JanusGraphManagement =>
      val el = mgmt.getEdgeLabel(name)
      el match {
        case null => throw new IllegalArgumentException(s"Edge label '$name' does not exists")
        case _ => el
      }
    }

    def addEdgeLabel(name: String, multiplicity: Multiplicity): GraphManagementAction[EdgeLabel] = GraphManagementAction { mgmt: JanusGraphManagement =>
      mgmt.containsEdgeLabel(name) match {
        case true =>
          throw new IllegalArgumentException(s"Edge label '$name' already exists")
        case false =>
          val maker = mgmt.makeEdgeLabel(name)
          val janusMultiplicity = org.janusgraph.core.Multiplicity.valueOf(multiplicity.janusName.toUpperCase)
          maker.multiplicity(janusMultiplicity)
          val el = maker.make()
          el
      }

    }

  }

}
