package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements._

import language.higherKinds

/**
  * Created by johann on 27/04/17.
  */

class SimpleVertexPropertyBase[Key, Value : ValidValue, MetaKey, MetaProp[K, V] <: Property[K, V, MetaProp]](
    override val key: Key,
    override val value: Value,
    override val metaProperties: Map[MetaKey, MetaProp[MetaKey, BoxedValue]]
) extends VertexProperty[Key, Value, MetaKey, MetaProp, SimpleVertexPropertyBase] {

  override def map[U: ValidValue](f: (Value) => U) = new SimpleVertexPropertyBase(key, f(value), metaProperties)

}
