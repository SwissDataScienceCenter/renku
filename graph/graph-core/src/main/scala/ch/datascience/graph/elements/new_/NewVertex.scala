package ch.datascience.graph.elements.new_

import ch.datascience.graph.elements.Vertex
import ch.datascience.graph.elements.detached.DetachedRichProperty
import ch.datascience.graph.elements.new_.impl.ImplNewVertex

/**
  * Created by johann on 29/05/17.
  */
trait NewVertex extends Vertex with NewElement {

  final type TempId = Int

  def tempId: TempId

  final type Prop = DetachedRichProperty

}

object NewVertex {

  def apply(
    tempId: NewVertex#TempId,
    types: Set[NewVertex#TypeId],
    properties: NewVertex#Properties
  ): NewVertex = ImplNewVertex(tempId, types, properties)

  def unapply(vertex: NewVertex): Option[(NewVertex#TempId, Set[NewVertex#TypeId], NewVertex#Properties)] = {
    if (vertex eq null)
      None
    else
      Some(vertex.tempId, vertex.types, vertex.properties)
  }

}
