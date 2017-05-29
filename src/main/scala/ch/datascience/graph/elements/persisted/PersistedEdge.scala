package ch.datascience.graph.elements.persisted

import ch.datascience.graph.bases.HasId
import ch.datascience.graph.elements.Edge
import ch.datascience.graph.Constants

/**
  * Created by johann on 29/05/17.
  */
trait PersistedEdge
  extends Edge
    with PersistedElement
    with HasId {

  final type Id = Constants.EdgeId

  final type VertexReference = PersistedVertex#Id

  final type PathType = EdgePath[VertexReference, Id]

  final type Prop = PersistedRecordProperty

  final def path: EdgePath[VertexReference, Id] = EdgePath(from, id)

}
