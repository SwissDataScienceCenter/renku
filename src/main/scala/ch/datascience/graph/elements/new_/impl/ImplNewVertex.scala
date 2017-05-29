//package ch.datascience.graph.elements.new_.impl
//
//import ch.datascience.graph.elements.detached.DetachedRichProperty
//import ch.datascience.graph.elements.new_.NewVertex
//import ch.datascience.graph.elements.{MultiPropertyValue, Property, RichProperty}
//import ch.datascience.graph.values.BoxedOrValidValue
//
///**
//  * Created by jeberle on 15.05.17.
//  */
//case class ImplNewVertex[T, K, V: BoxedOrValidValue](
//   tempId: NewVertex#TempId,
//   types: Set[T],
//   properties: Map[K, MultiPropertyValue[DetachedRichProperty[K, V]]]
// ) extends NewVertex {
//
// type TypeId = T
//
// type Prop = DetachedRichProperty[K, V]
//
//}
