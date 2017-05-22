package ch.datascience.graph.values

import ch.datascience.graph.types.DataType

/**
  * Trait for proving that a value of type V is either a [[BoxedValue]] or there is a proof that v is a valid value
  * (see: [[ValidValue]]).
  *
  * @tparam V analyzed type
  */
sealed trait BoxedOrValidValue[-V] {
  def isBoxed: Boolean

  def dataType[U <: V](v: U): DataType

  def castValue[U <: V](v: U): Either[BoxedValue, (U, ValidValue[U])]
}

object BoxedOrValidValue {

  implicit object BoxedValueEvidence extends BoxedOrValidValue[BoxedValue] {
    def isBoxed: Boolean = true

    def dataType[U <: BoxedValue](v: U): DataType = v.dataType

    def castValue[U <: BoxedValue](v: U): Either[BoxedValue, (U, ValidValue[U])] = Left(v)
  }

  implicit class ValidValueEvidence[V](evidence: ValidValue[V]) extends BoxedOrValidValue[V] {
    def isBoxed: Boolean = false

    def dataType[U <: V](v: U): DataType = evidence.dataType

    def castValue[U <: V](v: U): Either[BoxedValue, (U, ValidValue[U])] = Right((v, evidence))

    def validValue: ValidValue[V] = evidence
  }

  implicit def lift[V: ValidValue]: BoxedOrValidValue[V] = new ValidValueEvidence(implicitly[ValidValue[V]])

}
