package ch.datascience.graph.elements.persisted

import ch.datascience.graph.Constants
import ch.datascience.graph.bases.HasId
import ch.datascience.graph.elements.Edge
import ch.datascience.graph.elements.persisted.impl.ImplPersistedEdge

/**
  * Created by johann on 29/05/17.
  */
trait PersistedEdge
  extends Edge
    with PersistedElement
    with HasId {

  final type Id = Constants.EdgeId

  final type VertexReference = PersistedVertex#Id

  final type PathType = EdgePath

  final type Prop = PersistedRecordProperty

  final def path: EdgePath = EdgePath(id)

}

object PersistedEdge {

  def apply(
    id: PersistedEdge#Id,
    label: PersistedEdge#Label,
    from: PersistedEdge#VertexReference,
    to: PersistedEdge#VertexReference,
    properties: PersistedEdge#Properties
  ): PersistedEdge = ImplPersistedEdge(id, label, from, to, properties)

  def unapply(edge: PersistedEdge): Option[(PersistedEdge#Id, PersistedEdge#Label, PersistedEdge#VertexReference, PersistedEdge#VertexReference, PersistedEdge#Properties)] = {
    if (edge eq null)
      None
    else
      Some(edge.id, edge.label, edge.from, edge.to, edge.properties)
  }

}
