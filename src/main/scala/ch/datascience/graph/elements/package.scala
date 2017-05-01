package ch.datascience.graph

import language.higherKinds

/**
  * Created by johann on 28/04/17.
  */
package object elements {

  /**
    * Type used for properties
    *
    * @tparam Key key type
    * @tparam Value value type
    * @tparam Prop property type
    */
  type Properties[Key, +Value, +Prop <: Property[Key, Value, Prop]] = Map[Key, Property[Key, Value, Prop]]

  /**
    * Type used for multi-properties
    *
    * @tparam Key key type
    * @tparam Value value type
    * @tparam Prop property type
    */
  type MultiProperties[Key, +Value, +Prop <: Property[Key, Value, Prop]] = Map[Key, MultiPropertyValue[Key, Value, Property[Key, Value, Prop]]]

}
