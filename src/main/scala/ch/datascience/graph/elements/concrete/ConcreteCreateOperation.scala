package ch.datascience.graph.elements.concrete

import ch.datascience.graph.elements.{CreateOperation, HasId}
/**
  * Created by jeberle on 10.05.17.
  */
case class ConcreteCreateOperation[TemporaryId, +T <: HasId[TemporaryId]] (
  element: T
) extends CreateOperation[TemporaryId, T]

