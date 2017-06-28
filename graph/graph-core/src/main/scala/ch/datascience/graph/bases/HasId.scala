package ch.datascience.graph.bases

/**
  * Basic trait for elements that have an id.
  */
trait HasId {

  /**
    * type of identifier
    */
  type Id

  /**
    * The id
    *
    * @return the id
    */
  def id: Id

}
