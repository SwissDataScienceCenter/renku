package ch.datascience.graph.bases

import ch.datascience.graph.types.DataType
import ch.datascience.graph.values.BoxedOrValidValue

/**
  * Base trait for elements that hold a value.
  */
trait HasValue {

  /**
    * type of the value
    */
  type Value

  /**
    * The value
    *
    * @return the value
    */
  def value: Value

  def dataType(implicit e: BoxedOrValidValue[Value]): DataType = e.dataType(value)

}
