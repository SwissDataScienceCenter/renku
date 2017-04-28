package ch.datascience
package graph
package elements

import language.higherKinds

/**
  * Base trait for elements that hold properties
  *
  * Properties can be validated (see package types).
  *
  */
trait HasProperties[Key, Value, Prop[K, V] <: Property[K, V, Prop]] extends Element {

  implicit def validPropertyValuesEvidence: ValidValue[Value]

  val properties: Map[Key, Prop[Key, Value]]

}
