package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements.{BoxedValue, Vertex, VertexPropertyValues}

/**
  * Created by johann on 27/04/17.
  */
final case class SimpleVertex[TypeId, Key, MetaKey](
    override val types: Set[TypeId],
    override val properties: Map[Key, VertexPropertyValues[Key, BoxedValue, MetaKey]]
) extends Vertex[TypeId, Key, MetaKey]
