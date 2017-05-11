package ch.datascience.graph.elements.mutation

import ch.datascience.graph.elements.{HasPath, HasValue}

/**
  * Created by jeberle on 10.05.17.
  */
trait UpdateOperation[Path, +Value, +T <: HasPath[Path] with HasValue[Value, T]] {

}
