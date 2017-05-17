package ch.datascience.graph.elements.mutation

import ch.datascience.graph.elements.persistence.{HasPath, NewElement, Path}

/**
  * Created by jeberle on 10.05.17.
  */

trait CreateOperation[+T <: NewElement] extends Operation{

}
