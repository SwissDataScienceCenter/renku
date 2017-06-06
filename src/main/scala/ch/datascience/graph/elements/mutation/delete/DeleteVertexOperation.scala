package ch.datascience.graph.elements.mutation.delete

import ch.datascience.graph.elements.persisted.PersistedVertex

/**
  * Created by johann on 30/05/17.
  */
case class DeleteVertexOperation(vertex: PersistedVertex) extends DeleteOperation {

  final type ElementType = PersistedVertex

}
