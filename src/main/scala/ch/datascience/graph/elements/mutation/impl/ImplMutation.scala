package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.mutation.{Mutation, Operation}

/**
  * Created by johann on 08/06/17.
  */
private[mutation] class ImplMutation private (val operations: Seq[Operation]) extends Mutation {
  require(operations.nonEmpty, "empty mutation")

  override def toString: String = s"Mutation($operations)"

}

private[mutation] object ImplMutation {

  def apply(operations: Seq[Operation]): ImplMutation = new ImplMutation(operations)

}
