package ch.datascience.graph.elements.persisted.impl

import ch.datascience.graph.elements.persisted.PersistedEdge

/**
  * Created by jeberle on 15.05.17.
  */
private[persisted] case class ImplPersistedEdge(
  id: PersistedEdge#Id,
  from: PersistedEdge#VertexReference,
  to: PersistedEdge#VertexReference,
  types: Set[PersistedEdge#TypeId],
  properties: PersistedEdge#Properties
) extends PersistedEdge
