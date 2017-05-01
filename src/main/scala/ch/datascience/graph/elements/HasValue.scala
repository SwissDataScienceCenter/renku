package ch.datascience.graph.elements

import ch.datascience.graph.types.DataType

import scala.language.implicitConversions

/**
  * Base trait for elements that hold a value.
  *
  * @tparam Value value type
  * @tparam This self type
  */
trait HasValue[+Value, +This <: HasValueBase[Value]] extends HasValueBase[Value] { this: This =>

  def lifted: This = this

  def mapValue[U >: Value, V, That](f: (U) => V)(implicit e: HasValueMapper[U, This, V, That]): That = e.map(this)(f)

  def dataType(implicit e: BoxedOrValidValue[Value]): DataType = e.dataType(value)

  def boxed[U >: Value, That](implicit e1: BoxedOrValidValue[Value], e2: HasValueMapper[U, This, BoxedValue, That]): That = e2.map(this) { _ => boxedValue }

  @throws[java.lang.ClassCastException]
  def unboxedAs[U >: Value, V : ValidValue, That](implicit e1: BoxedOrValidValue[Value], e2: HasValueMapper[U, This, V, That]): That = {
    implicitly[ValidValue[V]].boxed(unboxedValueAs[V]).dataType // Force cast
    e2.map(this) { _ => unboxedValueAs[V] }
  }

  def boxedValue(implicit e: BoxedOrValidValue[Value]): BoxedValue = e.castValue(value) match {
    case Left(b) => b
    case Right((_, isV)) => isV.boxed(value)
  }

  @throws[java.lang.ClassCastException]
  def unboxedValueAs[V : ValidValue](implicit e: BoxedOrValidValue[Value]): V = e.castValue(value) match {
    case Left(b) => b.unboxAs[V]
    case Right((v, isV)) => v.asInstanceOf[V]
  }

}
