package ch.datascience.graph.elements

import ch.datascience.graph.types.DataType

/**
  * Created by johann on 27/04/17.
  */
trait HasValue[Value] {

  val value: Value

  implicit def validValueEvidence: ValidValue[Value]

  def dataType: DataType = implicitly[ValidValue[Value]].dataType(value)

  def boxedValue: BoxedValue = implicitly[ValidValue[Value]].boxed(value)

  def unboxedValue: Any = value match {
    case b: BoxedValue => b.self
    case _ => value
  }

}
