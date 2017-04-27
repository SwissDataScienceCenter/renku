package ch.datascience.graph.elements

/**
  * Created by johann on 27/04/17.
  */
trait Vertex[TypeId, Key, MetaKey] extends Element {

  /**
    * Set of type identifiers
    */
  val types: Set[TypeId]

  val properties: Map[Key, VertexPropertyValues[Key, BoxedValue, MetaKey]]

}
