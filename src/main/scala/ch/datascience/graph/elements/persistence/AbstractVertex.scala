package ch.datascience.graph.elements.persistence

import ch.datascience.graph.elements.simple.{SimpleProperty, SimpleVertexProperty, SimpleVertexPropertyBase}
import ch.datascience.graph.elements.{BoxedValue, HasId, Vertex, MultiPropertyValue}

/**
  * Created by johann on 28/04/17.
  */
abstract class AbstractVertex[Id, TypeId, Key, MetaKey](
    override val id: Id,
    override val types: Set[TypeId],
    override val properties: Vertex[TypeId, Key, MetaKey, SimpleProperty, SimpleVertexPropertyBase]#MultiPropertiesType
) extends Vertex[TypeId, Key, MetaKey, SimpleProperty, SimpleVertexPropertyBase]
  with HasId[Id]

object AbstractVertex {

//  type MultiPropertiesType[TypeId, Key, MetaKey] = Vertex[TypeId, Key, MetaKey, SimpleProperty, SimpleVertexPropertyBase]#MultiPropertiesType

}
