package ch.datascience.graph.elements.tinkerpop_mappers

import ch.datascience.graph.types.Cardinality
import org.apache.tinkerpop.gremlin.structure.VertexProperty

/**
  * Created by johann on 30/05/17.
  */
case object CardinalityWriter extends Writer[Cardinality, VertexProperty.Cardinality] {

  def write(cardinality: Cardinality): VertexProperty.Cardinality = cardinality match {
    case Cardinality.Single => VertexProperty.Cardinality.single
    case Cardinality.Set => VertexProperty.Cardinality.set
    case Cardinality.List => VertexProperty.Cardinality.list
  }

}
