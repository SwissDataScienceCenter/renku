package ch.datascience.graph.elements

import ch.datascience.graph.bases.HasKey
import ch.datascience.graph.types.{Cardinality, DataType}
import ch.datascience.graph.values.BoxedOrValidValue

/**
  * Representation of multi-property values
  *
  * Non-multi properties are represented using a map Key -> Property,
  * while multi-properties are represented using a map Key -> MultiPropertyValue.
  *
  * There are three types of multi-property values: SingleValue, SetValue and ListValue
  *
  * SingleValue: only one value. A map Key -> SingleValue is equivalent to a map Key -> Property.
  * SetValue: a set of values.
  * ListValue: a multiset of values. The name is misleading, but that's how tinkerpop works.
  *
  * @tparam Key   type of key
  * @tparam Value type of value, constrained to verify [[BoxedOrValidValue]]
  * @tparam Prop  type of underlying property
  */
sealed abstract class MultiPropertyValue[+Key, +Value: BoxedOrValidValue, +Prop <: Property[Key, Value]]
  extends HasKey[Key]
    with Element
    with Iterable[Prop] {

  //TODO: Builders

  def dataType: DataType = dataTypes.head

  protected final def dataTypes: Set[DataType] =
    this.asIterable.map( _.dataType(implicitly[BoxedOrValidValue[Value]]) ).toSet

  def asIterable: Iterable[Prop]

  final def iterator: Iterator[Prop] = asIterable.toIterator

  final def values: Iterable[Value] = asIterable.map(_.value)

  final def cardinality: Cardinality = this match {
    case SingleValue(_) => Cardinality.Single
    case SetValue(_)    => Cardinality.Set
    case ListValue(_)   => Cardinality.List
  }

}

case class SingleValue[+Key, +Value: BoxedOrValidValue, +Prop <: Property[Key, Value]](
  property: Prop
) extends MultiPropertyValue[Key, Value, Prop] {

  def key: Key = property.key

  def asIterable: List[Prop] = List(property)

}

case class SetValue[+Key, +Value: BoxedOrValidValue, +Prop <: Property[Key, Value]](
  properties: List[Prop]
) extends MultiPropertyValue[Key, Value, Prop] {
  require(keySet.size <= 1, s"Multiple keys detected: ${keySet.mkString(", ")}")
  require(dataTypes.size <= 1, s"Multiple datatypes detected: ${dataTypes.mkString(", ")}")
  require(
    properties.map(_.value).distinct.size == properties.size,
    s"Multiple values detected: ${properties.map(_.value).mkString(", ")}"
  )

  def key: Key = keySet.head

  private[this] def keySet: Set[Key] = properties.map(_.key).toSet

  def asIterable: Iterable[Prop] = properties

}

case class ListValue[+Key, +Value: BoxedOrValidValue, +Prop <: Property[Key, Value]](
  properties: List[Prop]
) extends MultiPropertyValue[Key, Value, Prop] {
  require(keySet.size <= 1, s"Multiple keys detected: ${keySet.mkString(", ")}")
  require(dataTypes.size <= 1, s"Multiple datatypes detected: ${dataTypes.mkString(", ")}")

  def key: Key = keySet.head

  private[this] def keySet: Set[Key] = properties.map(_.key).toSet

  def asIterable: List[Prop] = properties

}
