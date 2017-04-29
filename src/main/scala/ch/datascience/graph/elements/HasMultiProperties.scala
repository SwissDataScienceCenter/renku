package ch.datascience.graph.elements

import scala.language.higherKinds

/**
  * Base trait for elements that hold multi-properties (single, set, or list cardinality)
  *
  * Properties can be validated (see package types).
  *
  */
trait HasMultiProperties[Key, Value, Prop[K, V] <: Property[K, V, Prop]] extends Element {

  implicit def validMultiPropertyValuesEvidence: ValidValue[Value]

  type MultiPropertiesType = MultiProperties[Key, Value, Prop]
  val properties: MultiPropertiesType

}

//private[this] class HasMultiPropertiesHelper[Key, Prop[K, V] <: Property[K, V, Prop]] {
//  type PropertyV[V] = Prop[Key, V]
//}
