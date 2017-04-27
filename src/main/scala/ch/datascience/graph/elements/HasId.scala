package ch.datascience.graph.elements

/**
  * Trait for elements that have a unique identifier
  */
trait HasId[Id] {

  /**
    * The identifier
    */
  val id: Id

}
