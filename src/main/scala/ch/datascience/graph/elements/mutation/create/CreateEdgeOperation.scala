package ch.datascience.graph.elements.mutation.create

import ch.datascience.graph.elements.new_.NewEdge

/**
  * Created by johann on 30/05/17.
  */
case class CreateEdgeOperation(edge: NewEdge) extends CreateOperation {

  final type ElementType = NewEdge

}
