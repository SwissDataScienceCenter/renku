package ch.datascience.graph.elements

/**
  * Base trait for multi-records, i.e. elements that hold multi-properties (single, set, or list cardinality)
  *
  * Properties can be validated (see package types).
  *
  */
trait MultiRecord[Key, +Value, +Prop <: Property[Key, Value]] extends Element {

  def properties: MultiProperties[Key, Value, Prop]

  //TODO: define <|, like for [[Record]]

}
