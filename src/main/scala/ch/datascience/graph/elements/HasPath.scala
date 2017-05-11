package ch.datascience.graph.elements

/**
  * Basic trait for elements that have an path.
  * Usually children elements of the ones with an id.
  *
  * @tparam Path path type for the Element
  */
trait HasPath[+Path] {

  /**
    * The path
    *
    * @return the path
    */
  def path: Path

}
