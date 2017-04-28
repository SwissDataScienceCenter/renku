package ch.datascience.graph.elements

import ch.datascience.graph.types.DataType
import language.higherKinds

/**
  * Created by johann on 27/04/17.
  */
trait HasValue[Value, This[V] <: HasValue[V, This]] {
  val value: Value

  implicit def validValueEvidence: ValidValue[Value]

  def dataType: DataType = implicitly[ValidValue[Value]].dataType(value)

  def boxed: This[BoxedValue] = this map { _ => boxedValue }

  def unboxed: This[_] = value match {
    case b: BoxedValue => this.map({ _ => b.self })(b.isValidValue)
    case _ => this map{ x => x }
  }

  def boxedValue: BoxedValue = implicitly[ValidValue[Value]].boxed(value)

  def unboxedValue: Any = value match {
    case b: BoxedValue => b.self
    case _ => value
  }

  def map[U : ValidValue](f: (Value) => U): This[U]

}
