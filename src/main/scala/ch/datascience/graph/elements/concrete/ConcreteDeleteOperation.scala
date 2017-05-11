package ch.datascience.graph.elements.concrete

import ch.datascience.graph.elements.{CreateOperation, HasId}

/**
  * Created by jeberle on 10.05.17.
  */
case class ConcreteDeleteOperation[PersistedId, +T <: HasId[PersistedId]](
  element: T
) extends CreateOperation[PersistedId, T]

