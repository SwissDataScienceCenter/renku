package ch.datascience.graph.elements.mutation

import ch.datascience.graph.elements.HasValue
import ch.datascience.graph.elements.persistence.{HasPath, Path, PersistedElement}

/**
  * Created by jeberle on 10.05.17.
  */
trait UpdateOperation[+P <: Path, +Value, +T <: PersistedElement[P] with HasValue[Value, T]] {

}
