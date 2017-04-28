package ch.datascience.graph.elements

import language.higherKinds

/**
  * Created by johann on 27/04/17.
  */
trait Vertex[
    TypeId,
    Key,
    MetaKey,
    MetaProp[K, V] <: Property[K, V, MetaProp],
    VertexPropertyType[K, V, MK, MP[MPK, MPV] <: Property[MPK, MPV, MP]] <: VertexProperty[K, V, MK, MP, VertexPropertyType]
] extends Element {

  /**
    * Set of type identifiers
    */
  val types: Set[TypeId]

  val properties: Map[Key, VertexPropertyValues[Key, BoxedValue, MetaKey, MetaProp, VertexPropertyType]]

}
