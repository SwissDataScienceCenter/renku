package ch.datascience.graph.elements

import scala.language.higherKinds

/**
  * Base trait for records that have properties which are constrained by types
  *
  * Typed records can be validated (see package types).
  *
  */
trait TypedRecord[TypeId, Key, +Value, +Prop <: Property[Key, Value, Prop]]
  extends Record[Key, Value, Prop] {

  /**
    * Set of type identifiers
    */
  def types: Set[TypeId]

}
