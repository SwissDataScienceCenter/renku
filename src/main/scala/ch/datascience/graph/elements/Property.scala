package ch.datascience.graph.elements

import ch.datascience.graph.bases.{HasKey, HasValue}

/**
  * Base trait for property
  *
  * @tparam Key   key type
  * @tparam Value value type
  */
trait Property[+Key, +Value]
 extends HasKey[Key]
   with HasValue[Value]
   with Element
