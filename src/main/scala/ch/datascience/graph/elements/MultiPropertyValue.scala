package ch.datascience.graph.elements

import ch.datascience.graph.HasKey
import ch.datascience.graph.types.{Cardinality, DataType}

import scala.language.higherKinds

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
sealed abstract class MultiPropertyValue[+Key, +Value: BoxedOrValidValue, +Prop <: Property[Key, Value, Prop]]
  extends HasKey[Key]
    with Element
    with Iterable[Property[Key, Value, Prop]] {

  //TODO: Builders

  def dataType: DataType = dataTypes.head

  protected final def dataTypes: Set[DataType] =
    this.asIterable.map( _.dataType(implicitly[BoxedOrValidValue[Value]]) ).toSet

  def asIterable: Iterable[Property[Key, Value, Prop]]

  final def iterator: Iterator[Property[Key, Value, Prop]] = asIterable.toIterator

  final def values: Iterable[Value] = asIterable.map(_.value)

  def boxed[K >: Key, U >: Value, That <: Property[K, BoxedValue, That]](
    implicit e: HasValueMapper[U, Prop,
    BoxedValue, That]
  ): MultiPropertyValue[K, BoxedValue, That]

  final def cardinality: Cardinality = this match {
    case SingleValue(_) => Cardinality.Single
    case SetValue(_)    => Cardinality.Set
    case ListValue(_)   => Cardinality.List
  }

}

case class SingleValue[+Key, +Value: BoxedOrValidValue, +Prop <: Property[Key, Value, Prop]](
  property: Property[Key, Value, Prop]
) extends MultiPropertyValue[Key, Value, Prop] {

  def key: Key = property.key

  def asIterable: List[Property[Key, Value, Prop]] = List(property)

  def boxed[K >: Key, U >: Value, That <: Property[K, BoxedValue, That]](
    implicit e: HasValueMapper[U, Prop, BoxedValue, That]
  ): SingleValue[K, BoxedValue, That] = {
    SingleValue(property.boxed[U, That](implicitly[BoxedOrValidValue[Value]], e))
  }

}

case class SetValue[+Key, +Value: BoxedOrValidValue, +Prop <: Property[Key, Value, Prop]](
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

  def asIterable: Iterable[Property[Key, Value, Prop]] = properties

  def boxed[K >: Key, U >: Value, That <: Property[K, BoxedValue, That]](
    implicit e: HasValueMapper[U, Prop, BoxedValue, That]
  ): SetValue[K, BoxedValue, That] = {
    val boxedProperties = for (p <- properties) yield p.boxed[U, That](implicitly[BoxedOrValidValue[Value]], e)
    SetValue[K, BoxedValue, That](boxedProperties)
  }

}

case class ListValue[+Key, +Value: BoxedOrValidValue, +Prop <: Property[Key, Value, Prop]](
  properties: List[Prop]
) extends MultiPropertyValue[Key, Value, Prop] {
  require(keySet.size <= 1, s"Multiple keys detected: ${keySet.mkString(", ")}")
  require(dataTypes.size <= 1, s"Multiple datatypes detected: ${dataTypes.mkString(", ")}")

  def key: Key = keySet.head

  private[this] def keySet: Set[Key] = properties.map(_.key).toSet

  def asIterable: List[Property[Key, Value, Prop]] = properties

  def boxed[K >: Key, U >: Value, That <: Property[K, BoxedValue, That]](
    implicit e: HasValueMapper[U, Prop, BoxedValue, That]
  ): ListValue[K, BoxedValue, That] = {
    val boxedProperties = for (p <- properties) yield p.boxed[U, That](implicitly[BoxedOrValidValue[Value]], e)
    ListValue[K, BoxedValue, That](boxedProperties)
  }

}
