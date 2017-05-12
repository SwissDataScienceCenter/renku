package ch.datascience.graph.elements.mutation

import ch.datascience.graph.elements.persistence.{Path, PersistedElement}

/**
  * Created by jeberle on 10.05.17.
  */

trait DeleteOperation[+P <: Path, +T <: PersistedElement[P]] extends Operation{

}
