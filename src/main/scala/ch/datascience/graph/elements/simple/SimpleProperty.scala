package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements.{Property, ValidValue}

/**
  * Created by johann on 27/04/17.
  */
final case class SimpleProperty[Key, Value : ValidValue](
    override val key: Key,
    override val value: Value
) extends Property[Key, Value, SimpleProperty] {

  override def map[U: ValidValue](f: (Value) => U): SimpleProperty[Key, U] = SimpleProperty(key, f(value))

}
