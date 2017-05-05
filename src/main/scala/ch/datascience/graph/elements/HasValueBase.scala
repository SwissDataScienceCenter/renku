package ch.datascience.graph.elements

/**
  * Basic trait for elements that hold a value.
  *
  * @tparam Value value type
  */
trait HasValueBase[+Value] {

  /**
    * The value
    *
    * @return the value
    */
  def value: Value

}
