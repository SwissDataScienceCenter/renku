package ch.datascience.graph.elements

import ch.datascience.graph.types.DataType

/**
  * Created by johann on 27/04/17.
  */
abstract class Property[Key, Value : ValidValue] extends Element with HasValue[Value] {

  val key: Key

  val value: Value

}
