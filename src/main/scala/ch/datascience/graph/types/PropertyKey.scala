package ch.datascience.graph.types

import ch.datascience.graph.HasKey

/**
  * Base trait for property key definitions
  *
  * @tparam Key type of key
  */
trait PropertyKey[+Key] extends HasKey[Key] {

  def cardinality: Cardinality

  def dataType: DataType

}
