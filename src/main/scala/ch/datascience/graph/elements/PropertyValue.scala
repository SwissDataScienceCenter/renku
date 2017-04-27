package ch.datascience.graph.elements

import ch.datascience.graph.elements.PropertyValue.ValidValue

/**
  * Created by johann on 27/04/17.
  */
sealed abstract class PropertyValue[V : ValidValue] extends Element with Iterable[V] {

  // Allows easy iteration
  final def asSeq: Seq[V] = this match {
    case SingleValue(v) => Seq(v)
    case SetValue(set) => set.toSeq
    case ListValue(vs) => vs
  }

  override final def iterator: Iterator[V] = asSeq.toIterator

  final def headOptionAsValue: Option[SingleValue[V]] = {
    asSeq.headOption.map(x => SingleValue(x)(implicitly[ValidValue[V]]))
  }

  def asSetValue: SetValue[V] = SetValue(asSeq.toSet)(implicitly[ValidValue[V]])

  def asListValue: ListValue[V] = ListValue(asSeq.toList)(implicitly[ValidValue[V]])

}

sealed abstract class SingleValue[V : ValidValue](val value: V) extends PropertyValue[V] with Product1[V] with Serializable {
  override final def _1: V = value
  override final def toString(): String = super.toString()
}

sealed abstract class SetValue[V : ValidValue](val values: Set[V]) extends PropertyValue[V] with Product1[Set[V]] with Serializable {
  override final def _1: Set[V] = values
  override final def toString(): String = super.toString()
}

sealed abstract class ListValue[V : ValidValue](val values: List[V]) extends PropertyValue[V] with Product1[List[V]] with Serializable {
  override final def _1: List[V] = values
  override final def toString(): String = super.toString()
}


object PropertyValue {

  sealed trait ValidValue[V]

  // Valid value types
  implicit object StringIsValid extends ValidValue[String]
  implicit object CharIsValid extends ValidValue[Char]
  implicit object BooleanIsValid extends ValidValue[Boolean]
  implicit object ByteIsValid extends ValidValue[Byte]
  implicit object ShortIsValid extends ValidValue[Short]
  implicit object IntIsValid extends ValidValue[Int]
  implicit object LongIsValid extends ValidValue[Long]
  implicit object FloatIsValid extends ValidValue[Float]
  implicit object DoubleIsValid extends ValidValue[Double]

  def apply[V : ValidValue](value: V): SingleValue[V] = SingleValue(value)
  def apply[V : ValidValue](values: Set[V]): SetValue[V] = SetValue(values)
  def apply[V : ValidValue](values: List[V]): ListValue[V] = ListValue(values)
  def apply[V : ValidValue](value1: V, value2: V, values: V*): ListValue[V] = ListValue(value1 :: value2 :: values.toList)

}

object SingleValue {

  def apply[V : ValidValue](value: V): SingleValue[V] = SingleValueImpl(value)
  def unapply[V](v: SingleValue[V]): Option[V] = Some(v._1)

  private[this] final case class SingleValueImpl[V : ValidValue](override val value: V) extends SingleValue[V](value)

}

object SetValue {

  def apply[V : ValidValue](values: Set[V]): SetValue[V] = SetValueImpl(values)
  def apply[V : ValidValue](values: V*): SetValue[V] = SetValueImpl(values.toSet)
  def unapply[V](v: SetValue[V]): Option[Set[V]] = Some(v._1)

  private[this] final case class SetValueImpl[V : ValidValue](override val values: Set[V]) extends SetValue[V](values)

}

object ListValue {

  def apply[V : ValidValue](values: List[V]): ListValue[V] = ListValueImpl(values)
  def apply[V : ValidValue](values: V*): ListValue[V] = ListValueImpl(values.toList)
  def unapply[V](v: ListValue[V]): Option[List[V]] = Some(v._1)

  private[this] final case class ListValueImpl[V : ValidValue](override val values: List[V]) extends ListValue[V](values)

}
