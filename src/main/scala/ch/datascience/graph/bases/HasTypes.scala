package ch.datascience.graph.bases

/**
  * Base trait for elements that have types.
  *
  * @tparam TypeId type of type identifiers
  */
trait HasTypes[TypeId] {

  /**
    * Set of type identifiers
    */
  def types: Set[TypeId]

}
