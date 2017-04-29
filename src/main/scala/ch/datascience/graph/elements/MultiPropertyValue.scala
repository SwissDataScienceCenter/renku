package ch.datascience.graph.elements

import ch.datascience.graph.types.Cardinality

import language.higherKinds

/**
  * Created by johann on 27/04/17.
  */
sealed abstract class MultiPropertyValue[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]]
//sealed abstract class MultiPropertyValue[Value : ValidValue, Holder[V] <: HasValue[V, Holder]]
  extends Element with Iterable[Holder[Key, Value]] {
//  extends Element with Iterable[Holder[Value]] {

  // Allows easy iteration
  final def asSeq: Seq[Holder[Key, Value]] = this match {
    case SingleValue(holder) => List(holder)
    case SetValue(map) => map.values.toSeq
    case ListValue(values) => values
  }

  override final def iterator: Iterator[Holder[Key, Value]] = asSeq.toIterator

  final def boxed: MultiPropertyValue[Key, BoxedValue, Holder] = this match {
    case SingleValue(holder) => SingleValue(holder.boxed)
    case SetValue(map) => SetValue((for { h <- map.values } yield h.boxedValue -> h.boxed).toMap)
    case ListValue(values) => ListValue(for { h <- values } yield h.boxed)
  }

  final def cardinality: Cardinality = this match {
    case SingleValue(_) => Cardinality.Single
    case SetValue(_) => Cardinality.Set
    case ListValue(_) => Cardinality.List
  }

}

sealed abstract class SingleValue[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]](val holder: Holder[Key, Value])
  extends MultiPropertyValue[Key, Value, Holder] {
  override final def toString(): String = super.toString()
}

sealed abstract class SetValue[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]](val values: Map[Value, Holder[Key, Value]])
  extends MultiPropertyValue[Key, Value, Holder] {
  override final def toString(): String = super.toString()
}

sealed abstract class ListValue[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]](val values: List[Holder[Key, Value]])
  extends MultiPropertyValue[Key, Value, Holder] {
  override final def toString(): String = super.toString()
}

object SingleValue {

  def apply2[Key, Value : ValidValue, MetaKey, MetaProp[K, V] <: Property[K, V, MetaProp], Holder[K, V, MK] <: VertexProperty[K, V, MK, MetaProp, Holder]](holder: Holder[Key, Value, MetaKey]) = {
    SingleValue(holder: holder.type#PropertyKV[Key, Value])
  }

  def apply[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]](holder: Holder[Key, Value]): SingleValue[Key, Value, Holder] = SingleValueImpl(holder)

  def unapply[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]](arg: MultiPropertyValue[Key, Value, Holder]): Option[Holder[Key, Value]] = arg match {
    case SingleValueImpl(holder) => Some(holder)
    case _ => None
  }

  private[this] case class SingleValueImpl[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]](override val holder: Holder[Key, Value])
    extends SingleValue[Key, Value, Holder](holder)

}

object SetValue {

  def apply[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]](values: (Value, Holder[Key, Value])*): SetValue[Key, Value, Holder] = SetValue(values.toMap)

  def apply[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]](values: Map[Value, Holder[Key, Value]]): SetValue[Key, Value, Holder] = SetValueImpl(values)
  def unapply[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]](arg: MultiPropertyValue[Key, Value, Holder]): Option[Map[Value, Holder[Key, Value]]] = arg match {
    case SetValueImpl(values) => Some(values)
    case _ => None
  }

  private[this] case class SetValueImpl[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]](override val values: Map[Value, Holder[Key, Value]])
    extends SetValue[Key, Value, Holder](values)

}

object ListValue {

  def apply[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]](values: (Holder[Key, Value])*): ListValue[Key, Value, Holder] = ListValue(values.toList)

  def apply[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]](values: List[Holder[Key, Value]]): ListValue[Key, Value, Holder] = ListValueImpl(values)
  def unapply[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]](arg: MultiPropertyValue[Key, Value, Holder]): Option[List[Holder[Key, Value]]] = arg match {
    case ListValueImpl(values) => Some(values)
    case _ => None
  }

  private[this] case class ListValueImpl[Key, Value : ValidValue, Holder[K, V] <: Property[K, V, Holder]](override val values: List[Holder[Key, Value]])
    extends ListValue[Key, Value, Holder](values)

}
