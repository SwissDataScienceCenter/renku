package ch.datascience.graph.elements

import ch.datascience.graph.elements.mutation.Operation

/**
  * Created by jeberle on 10.05.17.
  */

trait CreateOperation[Id, +T <: HasId[Id]] extends Operation{

}
