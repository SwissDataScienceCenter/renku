package ch.datascience.graph.elements.mutation

import ch.datascience.graph.bases.HasValue
import ch.datascience.graph.elements.persisted.{Path, PersistedElement}

/**
  * Created by jeberle on 10.05.17.
  */
trait UpdateOperation[+P <: Path, +Value, +T <: PersistedElement with HasValue] {

}
