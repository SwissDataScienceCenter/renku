package ch.datascience.graph.elements

/**
  * Created by johann on 27/04/17.
  */
trait Property[Key, Value] extends Element {

  val key: Key

  val propertyValue: PropertyValue[Value]

}
