package ch.datascience.graph.elements.concrete

import ch.datascience.graph.elements.{BoxedOrValidValue, HasValueMapper, Properties, RichProperty}


/**
  * Created by julien on 10/05/17.
  */
final case class ConcreteRichProperty[+Key, +Value: BoxedOrValidValue, MetaKey, +MetaValue: BoxedOrValidValue](
  key: Key,
  value: Value,
  properties: Properties[MetaKey, MetaValue, ConcreteProperty[MetaKey, MetaValue]]
) extends RichProperty[Key, Value, MetaKey, MetaValue, ConcreteProperty[MetaKey, MetaValue], ConcreteRichProperty[Key,
  Value, MetaKey, MetaValue]]

object ConcreteRichProperty {

  lazy val reusableMapper: Mapper[Nothing, Nothing, Nothing, Nothing, Nothing] =
    new Mapper[Nothing, Nothing, Nothing, Nothing, Nothing]

  class Mapper[Key, MetaKey, MetaValue: BoxedOrValidValue, U, V: BoxedOrValidValue]
    extends HasValueMapper[U, ConcreteRichProperty[Key, U, MetaKey, MetaValue], V, ConcreteRichProperty[Key, V, MetaKey,
      MetaValue]] {
    def map(
      srp: ConcreteRichProperty[Key, U, MetaKey, MetaValue]
    )(
      f: (U) => V
    ): ConcreteRichProperty[Key, V, MetaKey, MetaValue] =
      ConcreteRichProperty(srp.key, f(srp.value), srp.properties)
  }

  implicit def canMap[Key, MetaKey, MetaValue: BoxedOrValidValue, U, V: BoxedOrValidValue]: HasValueMapper[U,
    ConcreteRichProperty[Key, U, MetaKey, MetaValue], V, ConcreteRichProperty[Key, V, MetaKey, MetaValue]] =
    reusableMapper.asInstanceOf[Mapper[Key, MetaKey, MetaValue, U, V]]

}
