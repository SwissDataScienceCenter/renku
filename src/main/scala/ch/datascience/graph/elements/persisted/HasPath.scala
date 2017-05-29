package ch.datascience.graph.elements.persisted

///**
//  * Basic trait for elements that have a path.
//  * Usually children elements of the ones with an id.
//  *
//  * @tparam P path type for the Element
//  */
trait HasPath {

  type PathType <: Path

  /**
    * The path
    *
    * @return the path
    */
  def path: PathType

}
