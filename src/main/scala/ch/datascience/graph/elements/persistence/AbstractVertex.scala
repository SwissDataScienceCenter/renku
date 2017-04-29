package ch.datascience.graph.elements.persistence

import ch.datascience.graph.elements.{HasId, Vertex}
import ch.datascience.graph.elements.simple.{SimpleProperty, SimpleVertexProperty}

/**
  * Created by johann on 28/04/17.
  */
abstract class AbstractVertex[Id, TypeId, Key, MetaKey](
    override val id: Id,
    override val types: Set[TypeId],
    override val properties: Vertex[TypeId, Key, MetaKey, SimpleProperty, SimpleVertexProperty]#MultiPropertiesType
) extends Vertex[TypeId, Key, MetaKey, SimpleProperty, SimpleVertexProperty]
  with HasId[Id]
