package ch.datascience.graph.elements.detached.impl

import ch.datascience.graph.elements.detached.DetachedEdge

/**
  * Created by johann on 29/05/17.
  */
case class ImplDetachedEdge(
  from: DetachedEdge#VertexReference,
  to: DetachedEdge#VertexReference,
  types: Set[DetachedEdge#TypeId],
  properties: DetachedEdge#Properties
) extends DetachedEdge
