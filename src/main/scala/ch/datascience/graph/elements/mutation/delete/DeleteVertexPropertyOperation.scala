package ch.datascience.graph.elements.mutation.delete

import ch.datascience.graph.elements.persisted.PersistedVertexProperty

/**
  * Created by johann on 30/05/17.
  */
case class DeleteVertexPropertyOperation(vertexProperty: PersistedVertexProperty) extends DeleteOperation {

  final type ElementType = PersistedVertexProperty

}
