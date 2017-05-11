package ch.datascience.graph.elements.concrete

import ch.datascience.graph.elements.{BoxedOrValidValue, HasPath, HasValueMapper, Property}

/**
  * Created by julien on 10/05/17.
  */
final case class ConcreteProperty[+Path, +Key, +Value: BoxedOrValidValue](path: Path, key: Key, value: Value)
  extends Property[Key, Value, ConcreteProperty[Path, Key, Value]]
  with HasPath[Path]

object ConcreteProperty {

  lazy val reusableMapper: Mapper[Nothing, Nothing, Nothing, Nothing] = new Mapper[Nothing, Nothing, Nothing, Nothing]()

  class Mapper[Path, Key, U, V: BoxedOrValidValue] extends HasValueMapper[U, ConcreteProperty[Path, Key, U], V,
    ConcreteProperty[Path, Key, V]] {
    def map(sp: ConcreteProperty[Path, Key, U])(f: (U) => V): ConcreteProperty[Path, Key, V] = ConcreteProperty(sp.path, sp.key, f(sp.value))
  }

  implicit def canMap[Path, Key, U, V: BoxedOrValidValue]: HasValueMapper[U, ConcreteProperty[Path, Key, U], V, ConcreteProperty[Path, Key, V]] =
    reusableMapper.asInstanceOf[Mapper[Path, Key, U, V]]

}
