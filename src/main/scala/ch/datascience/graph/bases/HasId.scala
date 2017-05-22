package ch.datascience.graph.bases

/**
  * Basic trait for elements that have an id.
  *
  * @tparam Id id type for the Element
  */
trait HasId[+Id] {

  /**
    * The id
    *
    * @return the id
    */
  def id: Id

}
