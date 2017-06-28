package ch.datascience.graph.elements.mutation

import ch.datascience.graph.elements.mutation.impl.ImplMutation

/**
  * Created by jeberle on 10.05.17.
  */
trait Mutation {

  def operations: Seq[Operation]

}

object Mutation {

  def apply(operations: Seq[Operation]): Mutation = ImplMutation(operations)
  
}
