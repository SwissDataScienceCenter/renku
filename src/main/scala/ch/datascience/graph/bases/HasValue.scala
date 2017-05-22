package ch.datascience.graph.bases

import ch.datascience.graph.types.DataType
import ch.datascience.graph.values.BoxedOrValidValue

/**
  * Base trait for elements that hold a value.
  *
  * @tparam Value value type
  */
trait HasValue[+Value] {

  /**
    * The value
    *
    * @return the value
    */
  def value: Value

  def dataType(implicit e: BoxedOrValidValue[Value]): DataType = e.dataType(value)

}
