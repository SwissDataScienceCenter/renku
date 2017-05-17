package ch.datascience.graph.types

import ch.datascience.graph.HasKey
import ch.datascience.graph.types.impl.ImplPropertyKey

/**
  * Base trait for property key definitions
  *
  * @tparam Key type of key
  */
trait PropertyKey[+Key] extends HasKey[Key] {

  def dataType: DataType

  def cardinality: Cardinality

  def simpleCopy: PropertyKey[Key] = PropertyKey(key, dataType, cardinality)

}

object PropertyKey {

  def apply[Key](key: Key, dataType: DataType, cardinality: Cardinality): PropertyKey[Key] = ImplPropertyKey(key, dataType, cardinality)

  def unapply[Key](propertyKey: PropertyKey[Key]): Option[(Key, DataType, Cardinality)] = {
    if (propertyKey eq null)
      None
    else
      Some(propertyKey.key, propertyKey.dataType, propertyKey.cardinality)
  }

}
