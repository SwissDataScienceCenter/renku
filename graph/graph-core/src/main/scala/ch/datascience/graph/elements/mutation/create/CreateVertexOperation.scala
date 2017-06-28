package ch.datascience.graph.elements.mutation.create

import ch.datascience.graph.elements.new_.NewVertex

/**
  * Created by johann on 30/05/17.
  */
case class CreateVertexOperation(vertex: NewVertex) extends CreateOperation {

  final type ElementType = NewVertex

}
