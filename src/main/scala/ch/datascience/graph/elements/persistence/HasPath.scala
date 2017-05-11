package ch.datascience.graph.elements.persistence

/**
  * Basic trait for elements that have an path.
  * Usually children elements of the ones with an id.
  *
  * @tparam P path type for the Element
  */
trait HasPath[+P <: Path] {

  /**
    * The path
    *
    * @return the path
    */
  def path: P

}
