package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements.{BoxedOrValidValue, HasValueMapper, Property}

/**
  * Created by johann on 27/04/17.
  */
final case class SimpleProperty[+Key, +Value: BoxedOrValidValue](key: Key, value: Value)
  extends Property[Key, Value, SimpleProperty[Key, Value]]

object SimpleProperty {

  lazy val reusableMapper: Mapper[Nothing, Nothing, Nothing] = new Mapper[Nothing, Nothing, Nothing]()

  class Mapper[Key, U, V: BoxedOrValidValue] extends HasValueMapper[U, SimpleProperty[Key, U], V,
    SimpleProperty[Key, V]] {
    def map(sp: SimpleProperty[Key, U])(f: (U) => V): SimpleProperty[Key, V] = SimpleProperty(sp.key, f(sp.value))
  }

  implicit def canMap[Key, U, V: BoxedOrValidValue]: HasValueMapper[U, SimpleProperty[Key, U], V, SimpleProperty[Key, V]] =
    reusableMapper.asInstanceOf[Mapper[Key, U, V]]

}
