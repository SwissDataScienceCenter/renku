package ch.datascience
package graph
package elements

import language.higherKinds

/**
  * Base trait for elements that hold properties
  *
  * Properties can be validated (see package types).
  *
  * @tparam Key key type
  * @tparam Value value type
  * @tparam Prop property type
  */
trait HasProperties[Key, +Value, +Prop <: Property[Key, Value, Prop]] extends Element {

  def properties: Properties[Key, Value, Prop]

}
