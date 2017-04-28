package ch.datascience.graph.elements.persistence

import ch.datascience.graph.elements.simple.{SimpleProperty, SimpleVertexProperty, SimpleVertexPropertyBase}
import ch.datascience.graph.elements.{BoxedValue, HasId, Vertex, VertexPropertyValues}

/**
  * Created by johann on 28/04/17.
  */
abstract class AbstractVertex[Id, TypeId, Key, MetaKey](
    override val id: Id,
    override val types: Set[TypeId],
    override val properties: Map[Key, AbstractVertex.PropertyType[Key, MetaKey]]
) extends Vertex[TypeId, Key, MetaKey, SimpleProperty, SimpleVertexPropertyBase]
  with HasId[Id] {

}

object AbstractVertex {

  type PropertyType[Key, MetaKey] = VertexPropertyValues[Key, BoxedValue, MetaKey, SimpleProperty, SimpleVertexPropertyBase]

}
