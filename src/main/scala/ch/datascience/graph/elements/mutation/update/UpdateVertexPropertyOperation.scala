package ch.datascience.graph.elements.mutation.update

import ch.datascience.graph.elements.persisted.PersistedVertexProperty

/**
  * Created by johann on 20/06/17.
  */
case class UpdateVertexPropertyOperation(vertexProperty: PersistedVertexProperty, newValue: PersistedVertexProperty#Value) extends UpdateOperation {

  final type ElementType = PersistedVertexProperty

}
