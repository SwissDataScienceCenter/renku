package ch.datascience.graph.elements

import ch.datascience.graph.bases.{HasKey, HasValue}
import ch.datascience.graph.Constants

trait Property extends HasKey with HasValue with Element {

  final type Key = Constants.Key

  final type Value = Constants.Value

}

///**
//  * Base trait for property
//  *
//  * @tparam Key   key type
//  * @tparam Value value type
//  */
//trait Property[+Key, +Value]
// extends HasKey[Key]
//   with HasValue[Value]
//   with Element
