package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements.{BoxedValue, ValidValue, VertexProperty}

import scala.language.implicitConversions

/**
  * Created by johann on 28/04/17.
  */
final case class SimpleVertexProperty[Key, Value : ValidValue, MetaKey](
    override val key: Key,
    override val value: Value,
    override val properties: Map[MetaKey, SimpleProperty[MetaKey, BoxedValue]]
) extends VertexProperty[Key, Value, MetaKey, SimpleProperty, SimpleVertexProperty] {

  override def map[U: ValidValue](f: (Value) => U): SimpleVertexProperty[Key, U, MetaKey] = SimpleVertexProperty(key, f(value), properties)

}
