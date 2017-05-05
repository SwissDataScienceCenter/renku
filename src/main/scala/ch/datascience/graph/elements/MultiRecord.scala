package ch.datascience.graph.elements

import scala.language.higherKinds

/**
  * Base trait for multi-records, i.e. elements that hold multi-properties (single, set, or list cardinality)
  *
  * Properties can be validated (see package types).
  *
  */
trait MultiRecord[Key, +Value, +Prop <: Property[Key, Value, Prop]] extends Element {

  def properties: MultiProperties[Key, Value, Prop]

}
