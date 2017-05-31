package ch.datascience.graph.elements

import ch.datascience.graph.Constants
import ch.datascience.graph.bases.{HasKey, HasValue}

trait Property extends HasKey with HasValue with Element {

  final type Key = Constants.Key

  final type Value = Constants.Value

}

object Property {

  def unapply(prop: Property): Option[(Property#Key, Property#Value)] = {
    if (prop eq null)
      None
    else
      Some(prop.key, prop.value)
  }

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
