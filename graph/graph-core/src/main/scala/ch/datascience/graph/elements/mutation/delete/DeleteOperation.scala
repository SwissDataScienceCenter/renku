package ch.datascience.graph.elements.mutation.delete

import ch.datascience.graph.elements.mutation.Operation
import ch.datascience.graph.elements.persisted.PersistedElement

/**
  * Created by jeberle on 10.05.17.
  */

trait DeleteOperation extends Operation{

  type ElementType <: PersistedElement

}
