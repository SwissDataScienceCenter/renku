package ch.datascience.graph.elements.concrete

import ch.datascience.graph.elements.mutation.UpdateOperation
import ch.datascience.graph.elements.{CreateOperation, HasId, Property}

/**
  * Created by jeberle on 10.05.17.
  */
case class ConcreteUpdateOperation[+Path, +Key, +Value, +T <: ConcreteProperty[Path, Key, Value]](


) extends UpdateOperation[Path, Value, T]

