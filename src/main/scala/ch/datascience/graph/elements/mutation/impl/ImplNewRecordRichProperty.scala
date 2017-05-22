package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.persistence.{NewRecordProperty, Path}
import ch.datascience.graph.elements.{Properties, RichProperty}
import ch.datascience.graph.values.BoxedOrValidValue

/**
  * Created by johann on 11/05/17.
  */
case class ImplNewRecordRichProperty[Key, +Value: BoxedOrValidValue, +MetaValue: BoxedOrValidValue](
  parent: Path,
  key: Key,
  value: Value,
  properties: Properties[Key, MetaValue, ImplNewRecordProperty[Key, MetaValue]]
) extends NewRecordProperty[Key, Value]
  with RichProperty[Key, Value, MetaValue, ImplNewRecordProperty[Key, MetaValue]]
