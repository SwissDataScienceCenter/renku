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
    label: DetachedEdge#Label,
    from: DetachedEdge#VertexReference,
    to: DetachedEdge#VertexReference,
    properties: DetachedEdge#Properties
  ): DetachedEdge = ImplDetachedEdge(label, from, to, properties)

  def unapply(edge: DetachedEdge): Option[(DetachedEdge#Label, DetachedEdge#VertexReference, DetachedEdge#VertexReference, DetachedEdge#Properties)] = {
    if (edge eq null)
      None
    else
      Some(edge.label, edge.from, edge.to, edge.properties)
  }

}
