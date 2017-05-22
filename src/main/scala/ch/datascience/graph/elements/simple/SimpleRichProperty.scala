package ch.datascience.graph.elements.simple

import ch.datascience.graph.elements.{Properties, RichProperty}
import ch.datascience.graph.values.BoxedOrValidValue


/**
  * Created by johann on 28/04/17.
  */
final case class SimpleRichProperty[Key, +Value: BoxedOrValidValue, +MetaValue: BoxedOrValidValue](
  key: Key,
  value: Value,
  properties: Properties[Key, MetaValue, SimpleProperty[Key, MetaValue]]
) extends RichProperty[Key, Value, MetaValue, SimpleProperty[Key, MetaValue]]
