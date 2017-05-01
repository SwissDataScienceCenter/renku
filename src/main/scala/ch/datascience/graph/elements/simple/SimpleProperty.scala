package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements.{BoxedOrValidValue, HasValueMapper, Property}

/**
  * Created by johann on 27/04/17.
  */
final case class SimpleProperty[+Key, +Value : BoxedOrValidValue](key: Key, value: Value)
  extends Property[Key, Value, SimpleProperty[Key, Value]]

object SimpleProperty {

  class Mapper[Key, Value, V : BoxedOrValidValue] extends HasValueMapper[Value, SimpleProperty[Key, Value], V, SimpleProperty[Key, V]] {
    def map(sp: SimpleProperty[Key, Value])(f: (Value) => V): SimpleProperty[Key, V] = SimpleProperty(sp.key, f(sp.value))
  }

  lazy val reusableMapper: Mapper[Nothing, Nothing, Nothing] = new Mapper[Nothing, Nothing, Nothing]()

  implicit def canMap[Key, U, V : BoxedOrValidValue]: HasValueMapper[U, SimpleProperty[Key, U], V, SimpleProperty[Key, V]] = reusableMapper.asInstanceOf[Mapper[Key, U, V]]

}
