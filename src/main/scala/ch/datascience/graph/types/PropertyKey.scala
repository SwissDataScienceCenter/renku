package ch.datascience.graph.types

import ch.datascience.graph.HasKey
import ch.datascience.graph.types.impl.ImplPropertyKey

/**
  * Base trait for property key definitions
  *
  * @tparam Key type of key
  */
trait PropertyKey[+Key] extends HasKey[Key] {

  def cardinality: Cardinality

  def dataType: DataType

  def simpleCopy: PropertyKey[Key] = PropertyKey(key, cardinality, dataType)

}

object PropertyKey {

  def apply[Key](key: Key, cardinality: Cardinality, dataType: DataType): PropertyKey[Key] = ImplPropertyKey(key, cardinality, dataType)

  def unapply[Key](propertyKey: PropertyKey[Key]): Option[(Key, Cardinality, DataType)] = {
    if (propertyKey eq null)
      None
    else
      Some(propertyKey.key, propertyKey.cardinality, propertyKey.dataType)
  }

}
