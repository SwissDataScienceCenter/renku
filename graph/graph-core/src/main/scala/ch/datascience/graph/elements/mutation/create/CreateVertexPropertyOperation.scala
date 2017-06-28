package ch.datascience.graph.elements.mutation.create

import ch.datascience.graph.elements.new_.NewRichProperty

/**
  * Created by johann on 30/05/17.
  */
case class CreateVertexPropertyOperation(vertexProperty: NewRichProperty) extends CreateOperation {

  final type ElementType = NewRichProperty

}
