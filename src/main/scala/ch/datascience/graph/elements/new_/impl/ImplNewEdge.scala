package ch.datascience.graph.elements.new_.impl

import ch.datascience.graph.elements.new_.NewEdge

/**
  * Created by jeberle on 15.05.17.
  */
private[new_] case class ImplNewEdge(
  from: NewEdge#VertexReference,
  to: NewEdge#VertexReference,
  types: Set[NewEdge#TypeId],
  properties: NewEdge#Properties
) extends NewEdge
