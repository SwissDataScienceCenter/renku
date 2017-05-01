package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements.{BoxedOrValidValue, HasValueMapper, Properties, RichProperty}


/**
  * Created by johann on 28/04/17.
  */
final case class SimpleRichProperty[+Key, +Value : BoxedOrValidValue, MetaKey, +MetaValue : BoxedOrValidValue](
    key: Key,
    value: Value,
    properties: Properties[MetaKey, MetaValue, SimpleProperty[MetaKey, MetaValue]]
) extends RichProperty[Key, Value, MetaKey, MetaValue, SimpleProperty[MetaKey, MetaValue], SimpleRichProperty[Key, Value, MetaKey, MetaValue]]

object SimpleRichProperty {

  class Mapper[Key, MetaKey, MetaValue : BoxedOrValidValue, U, V : BoxedOrValidValue] extends HasValueMapper[U, SimpleRichProperty[Key, U, MetaKey, MetaValue], V, SimpleRichProperty[Key, V, MetaKey, MetaValue]] {
    def map(srp: SimpleRichProperty[Key, U, MetaKey, MetaValue])(f: (U) => V): SimpleRichProperty[Key, V, MetaKey, MetaValue] = SimpleRichProperty(srp.key, f(srp.value), srp.properties)
  }

  lazy val reusableMapper: Mapper[Nothing, Nothing, Nothing, Nothing, Nothing] = new Mapper[Nothing, Nothing, Nothing, Nothing, Nothing]()

  implicit def canMap[Key, MetaKey, MetaValue : BoxedOrValidValue, U, V : BoxedOrValidValue]: HasValueMapper[U, SimpleRichProperty[Key, U, MetaKey, MetaValue], V, SimpleRichProperty[Key, V, MetaKey, MetaValue]] = reusableMapper.asInstanceOf[Mapper[Key, MetaKey, MetaValue, U, V]]

}
