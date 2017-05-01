package ch.datascience.graph.typecheck

import ch.datascience.graph.types.PropertyKey

sealed trait Checked

trait PropertyChecked[+Key] extends Checked {

  /**
    * The definition of the validated property
    * @return property key
    */
  def propertyKey: PropertyKey[Key]

}

trait HasPropertiesChecked[Key] extends Checked {

  /**
    * The definitions of the validated properties
    * @return property key map
    */
  def propertyKeys: Map[Key, PropertyKey[Key]]

}
