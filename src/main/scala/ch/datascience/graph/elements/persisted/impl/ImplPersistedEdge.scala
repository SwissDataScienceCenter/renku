package ch.datascience.graph.elements.persisted.impl

import ch.datascience.graph.Constants.{EdgeId, VertexId}
import ch.datascience.graph.elements.persisted.{PersistedEdge, PersistedRecordProperty, PersistedVertex}
import ch.datascience.graph.values.BoxedOrValidValue

/**
  * Created by jeberle on 15.05.17.
  */
case class ImplPersistedEdge[I, VI, K](
  id: EdgeId,
  from: VertexId,
  to: VertexId,
  types: Set[PersistedEdge#TypeId],
  properties: Map[PersistedEdge#Prop#Key, PersistedEdge#Prop]
) extends PersistedEdge
