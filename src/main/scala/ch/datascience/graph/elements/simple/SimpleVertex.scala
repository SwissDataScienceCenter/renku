package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements.{BoxedValue, Vertex, VertexPropertyValues}

/**
  * Created by johann on 27/04/17.
  */
final case class SimpleVertex[TypeId, Key, MetaKey](
    override val types: Set[TypeId],
    override val properties: Map[Key, SimpleVertex.PropertyType[Key, MetaKey]]
) extends Vertex[TypeId, Key, MetaKey, SimpleProperty, SimpleVertexPropertyBase]

object SimpleVertex {

  type PropertyType[Key, MetaKey] = VertexPropertyValues[Key, BoxedValue, MetaKey, SimpleProperty, SimpleVertexPropertyBase]

}
