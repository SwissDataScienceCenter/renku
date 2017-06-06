package ch.datascience.graph.elements.mutation.update

import ch.datascience.graph.bases.HasValue
import ch.datascience.graph.elements.persisted.PersistedElement

/**
  * Created by jeberle on 10.05.17.
  */
trait UpdateOperation[+Value] {

  type ElementType <: PersistedElement with HasValue

}
