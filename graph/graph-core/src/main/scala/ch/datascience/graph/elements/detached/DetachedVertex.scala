package ch.datascience.graph.elements.detached

import ch.datascience.graph.elements.Vertex
import ch.datascience.graph.elements.detached.impl.ImplDetachedVertex

/**
  * Created by johann on 27/04/17.
  */
trait DetachedVertex extends Vertex {

  final type Prop = DetachedRichProperty

}

object DetachedVertex {

  def apply(
    types: Set[DetachedVertex#TypeId],
    properties: DetachedVertex#Properties
  ): DetachedVertex = ImplDetachedVertex(types, properties)

  def unapply(vertex: DetachedVertex): Option[(Set[DetachedVertex#TypeId], DetachedVertex#Properties)] = {
    if (vertex eq null)
      None
    else
      Some(vertex.types, vertex.properties)
  }
  
}
