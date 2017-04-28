package ch.datascience.graph.elements.persistence

import ch.datascience.graph.elements.{BoxedValue, HasId, Vertex, VertexPropertyValues}

/**
  * Created by johann on 27/04/17.
  */
final case class PersistedVertex[Id, TypeId, Key, MetaKey](
    override val id: Id,
    override val types: Set[TypeId],
    override val properties: Map[Key, AbstractVertex.PropertyType[Key, MetaKey]]
) extends AbstractVertex[Id, TypeId, Key, MetaKey](id, types, properties)
