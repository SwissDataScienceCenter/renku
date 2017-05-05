package ch.datascience.graph.elements

import scala.language.higherKinds

/**
  * Base trait for multi-records that have multi-properties which are constrained by types
  *
  * Typed  multi-records can be validated (see package types).
  *
  */
trait TypedMultiRecord[TypeId, Key, +Value, +Prop <: Property[Key, Value, Prop]]
  extends MultiRecord[Key, Value, Prop] {

  /**
    * Set of type identifiers
    */
  def types: Set[TypeId]

}
