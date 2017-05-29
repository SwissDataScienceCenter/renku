package ch.datascience.graph.elements.new_

import ch.datascience.graph.elements.Edge
import ch.datascience.graph.elements.persisted.PersistedVertex

/**
  * Created by johann on 29/05/17.
  */
trait NewEdge extends Edge with NewElement {

  type PersistedVertexType <: PersistedVertex

  type NewVertexType <: NewVertex

  final type VertexReference = Either[NewVertexType#TempId, PersistedVertexType#Id]

}
