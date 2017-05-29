//package ch.datascience.graph.elements.new_.impl
//
//import ch.datascience.graph.elements.detached.DetachedProperty
//import ch.datascience.graph.elements.new_.{NewEdge, NewVertex}
//import ch.datascience.graph.elements.persisted.PersistedVertex
//import ch.datascience.graph.values.BoxedOrValidValue
//
///**
//  * Created by jeberle on 15.05.17.
//  */
//case class ImplNewEdge[VI, T, K, V: BoxedOrValidValue](
//  from: Either[NewVertex#TempId, VI],
//  to: Either[NewVertex#TempId, VI],
//  types: Set[T],
//  properties: Map[K, DetachedProperty[K, V]]
// ) extends NewEdge {
//
//  type NewVertexType = NewVertex
//
//  type PersistedVertexType = PersistedVertex { type Id = VI }
//
//  type TypeId = T
//
//  type Prop = DetachedProperty[K, V]
//
//}
