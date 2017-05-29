package ch.datascience.graph.bases

/**
  * Base trait for elements that have types.
  */
trait HasTypes {

  /**
    * type of type identifiers
    */
  type TypeId

  /**
    * Set of type identifiers
    */
  def types: Set[TypeId]

}
