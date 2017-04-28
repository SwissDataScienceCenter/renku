//package ch.datascience.graph.elements.persistence
//
//import ch.datascience.graph.elements.{BoxedValue, HasId, Vertex, VertexPropertyValues}
//
///**
//  * Created by johann on 27/04/17.
//  */
//final case class NewVertex[TempId, TypeId, Key, MetaKey](
//    override val id: TempId,
//    override val types: Set[TypeId],
//    override val properties: Map[Key, VertexPropertyValues[Key, BoxedValue, MetaKey]]
//) extends Vertex[TypeId, Key, MetaKey]
//with HasId[TempId]
