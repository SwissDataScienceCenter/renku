package ch.datascience.graph.types

import ch.datascience.graph.bases.HasKey
import ch.datascience.graph.types.impl.ImplPropertyKey
import ch.datascience.graph.Constants

trait PropertyKey extends HasKey {

  final type Key = Constants.Key

  def dataType: DataType

  def cardinality: Cardinality

}

///**
//  * Base trait for property key definitions
//  *
//  * @tparam Key type of key
//  */
//trait PropertyKey[+Key] extends HasKey[Key] {
//
//  def dataType: DataType
//
//  def cardinality: Cardinality
//
//  def simpleCopy: PropertyKey[Key] = PropertyKey(key, dataType, cardinality)
//
//}

object PropertyKey {

  def apply(key: PropertyKey#Key, dataType: DataType, cardinality: Cardinality): PropertyKey = ImplPropertyKey(key, dataType, cardinality)

  def unapply(propertyKey: PropertyKey): Option[(propertyKey.Key, DataType, Cardinality)] = {
    if (propertyKey eq null)
      None
    else
      Some(propertyKey.key, propertyKey.dataType, propertyKey.cardinality)
  }

//  def apply[Key](key: Key, dataType: DataType, cardinality: Cardinality): PropertyKey[Key] = ImplPropertyKey(key, dataType, cardinality)
//
//  def unapply[Key](propertyKey: PropertyKey[Key]): Option[(Key, DataType, Cardinality)] = {
//    if (propertyKey eq null)
//      None
//    else
//      Some(propertyKey.key, propertyKey.dataType, propertyKey.cardinality)
//  }

}
