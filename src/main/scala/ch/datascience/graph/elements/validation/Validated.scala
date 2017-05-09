package ch.datascience.graph.elements.validation

import ch.datascience.graph.elements.{Property, Record}
import ch.datascience.graph.types.PropertyKey

sealed trait Validated

trait ValidatedProperty[+Key, +Value, +Prop <: Property[Key, Value, Prop]] extends Validated {

  /**
    * The validated property
    * @return property
    */
  def property: Property[Key, Value, Prop]

  /**
    * The definition of the validated property
    *
    * @return property key
    */
  def propertyKey: PropertyKey[Key]

}

trait ValidatedRecord[Key, +Value, +Prop <: Property[Key, Value, Prop]] extends Validated {

  /**
    * The validated record
    * @return
    */
  def record: Record[Key, Value, Prop]

  /**
    * The definitions of the validated properties
    *
    * @return property key map
    */
  def propertyKeys: Map[Key, PropertyKey[Key]]

}
