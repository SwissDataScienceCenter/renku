package ch.datascience.graph.elements.mutation.create

import ch.datascience.graph.elements.mutation.Operation
import ch.datascience.graph.elements.new_.NewElement

/**
  * Created by jeberle on 10.05.17.
  */

trait CreateOperation extends Operation {

  type ElementType <: NewElement

}
