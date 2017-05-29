//package ch.datascience.graph.elements.new_.impl
//
//import ch.datascience.graph.elements.detached.DetachedProperty
//import ch.datascience.graph.elements.new_.NewRichProperty
//import ch.datascience.graph.elements.persisted.Path
//import ch.datascience.graph.values.BoxedOrValidValue
//
///**
//  * Created by johann on 11/05/17.
//  */
//case class ImplNewRichProperty[K, V: BoxedOrValidValue](
//  parent: Path,
//  key: K,
//  value: V,
//  properties: Map[K, DetachedProperty[K, V]]
//) extends NewRichProperty {
//
//  type Key = K
//
//  type Value = V
//
//  type Prop = DetachedProperty[K, V]
//
//}
