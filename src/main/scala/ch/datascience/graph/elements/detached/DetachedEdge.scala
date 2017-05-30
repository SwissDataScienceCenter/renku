package ch.datascience.graph.elements.detached

import ch.datascience.graph.elements.detached.impl.ImplDetachedEdge
import ch.datascience.graph.elements.{Edge, Vertex}

/**
  * Created by johann on 29/05/17.
  */
trait DetachedEdge extends Edge {

  final type VertexReference = Vertex

  final type Prop = DetachedProperty

}

object DetachedEdge {

  def apply(
    from: DetachedEdge#VertexReference,
    to: DetachedEdge#VertexReference,
    types: Set[DetachedEdge#TypeId],
    properties: DetachedEdge#Properties
  ): DetachedEdge = ImplDetachedEdge(from, to, types, properties)

  def unapply(edge: DetachedEdge): Option[(DetachedEdge#VertexReference, DetachedEdge#VertexReference, Set[DetachedEdge#TypeId], DetachedEdge#Properties)] = {
    if (edge eq null)
      None
    else
      Some(edge.from, edge.to, edge.types, edge.properties)
  }

}
