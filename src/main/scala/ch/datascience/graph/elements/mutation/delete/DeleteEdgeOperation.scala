package ch.datascience.graph.elements.mutation.delete

import ch.datascience.graph.elements.persisted.PersistedEdge

/**
  * Created by johann on 30/05/17.
  */
case class DeleteEdgeOperation(edge: PersistedEdge) extends DeleteOperation {

  final type ElementType = PersistedEdge

}
