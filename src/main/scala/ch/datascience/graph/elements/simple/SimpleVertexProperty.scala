package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements.{BoxedValue, ValidValue}

/**
  * Created by johann on 28/04/17.
  */
final case class SimpleVertexProperty[Key, Value : ValidValue, MetaKey](
    override val key: Key,
    override val value: Value,
    override val properties: Map[MetaKey, SimpleProperty[MetaKey, BoxedValue]]
) extends SimpleVertexPropertyBase[Key, Value, MetaKey, SimpleProperty](key, value, properties) {

  type SimpleVertexPropertyKV[K, V] = SimpleVertexProperty[K, V, MetaKey]

}

object SimpleVertexProperty {

  def apply[Key, Value: ValidValue, MetaKey](key: Key, value: Value): SimpleVertexProperty[Key, Value, MetaKey] = {
    SimpleVertexProperty(key, value, Map.empty)
  }

}