package ch.datascience.graph.elements.new_

import ch.datascience.graph.elements.Edge
import ch.datascience.graph.elements.detached.DetachedProperty
import ch.datascience.graph.elements.new_.impl.ImplNewEdge
import ch.datascience.graph.elements.persisted.PersistedVertex

/**
  * Created by johann on 29/05/17.
  */
trait NewEdge extends Edge with NewElement {

  final type PersistedVertexType = PersistedVertex

  final type NewVertexType = NewVertex

  final type VertexReference = Either[NewVertexType#TempId, PersistedVertexType#Id]

  final type Prop = DetachedProperty

}

object NewEdge {

  def apply(
    label: NewEdge#Label,
    from: NewEdge#VertexReference,
    to: NewEdge#VertexReference,
    properties: NewEdge#Properties
  ): NewEdge = ImplNewEdge(label, from, to, properties)

  def unapply(edge: NewEdge): Option[(NewEdge#Label, NewEdge#VertexReference, NewEdge#VertexReference, NewEdge#Properties)] = {
    if (edge eq null)
      None
    else
      Some(edge.label, edge.from, edge.to, edge.properties)
  }

}
