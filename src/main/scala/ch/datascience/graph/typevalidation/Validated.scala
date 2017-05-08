package ch.datascience.graph.typevalidation

import ch.datascience.graph.types.PropertyKey

sealed trait Validated

trait ValidatedProperty[+Key] extends Validated {

  /**
    * The definition of the validated property
    *
    * @return property key
    */
  def propertyKey: PropertyKey[Key]

}

trait ValidatedRecord[Key] extends Validated {

  /**
    * The definitions of the validated properties
    *
    * @return property key map
    */
  def propertyKeys: Map[Key, PropertyKey[Key]]

}
