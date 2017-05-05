package ch.datascience.graph.elements

/**
  * Base trait for property
  *
  * @tparam Key   key type
  * @tparam Value value type
  * @tparam This  self type
  */
trait Property[+Key, +Value, +This <: PropertyBase[Key, Value]]
  extends PropertyBase[Key, Value]
    with HasValue[Value, This]
    with Element {
  this: This =>
}
