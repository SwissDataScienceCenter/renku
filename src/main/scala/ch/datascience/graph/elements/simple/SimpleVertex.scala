package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements._

/**
  * Created by johann on 27/04/17.
  */
final case class SimpleVertex[TypeId, Key, MetaKey](
    override val types: Set[TypeId],
    override val properties: Vertex[TypeId, Key, MetaKey, SimpleProperty, SimpleVertexProperty]#MultiPropertiesType
) extends Vertex[TypeId, Key, MetaKey, SimpleProperty, SimpleVertexProperty]
