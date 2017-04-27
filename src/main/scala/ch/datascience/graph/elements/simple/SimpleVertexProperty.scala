package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements.{BoxedValue, Property, ValidValue, VertexProperty}

/**
  * Created by johann on 27/04/17.
  */
final case class SimpleVertexProperty[Key, Value : ValidValue, MetaKey](
    override val key: Key,
    override val value: Value,
    override val metaProperties: Map[MetaKey, Property[MetaKey, BoxedValue]]
) extends VertexProperty[Key, Value, MetaKey] {

  override def validValueEvidence: ValidValue[Value] = implicitly[ValidValue[Value]]

  override def boxed: VertexProperty[Key, BoxedValue, MetaKey] = SimpleVertexProperty(key, boxedValue, metaProperties)

}

object SimpleVertexProperty {

  def apply[Key, Value : ValidValue, MetaKey](key: Key, value: Value): SimpleVertexProperty[Key, Value, MetaKey] = {
    SimpleVertexProperty(key, value, Map.empty)
  }

}
