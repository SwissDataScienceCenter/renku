package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.persistence.{NewMultiRecordProperty, Path}
import ch.datascience.graph.elements.{Properties, RichProperty}
import ch.datascience.graph.values.BoxedOrValidValue

/**
  * Created by johann on 11/05/17.
  */
case class ImplNewMultiRecordRichProperty[Key, +Value: BoxedOrValidValue, +MetaValue: BoxedOrValidValue](
  parent: Path,
  tempId: Int,
  key: Key,
  value: Value,
  properties: Properties[Key, MetaValue, ImplNewRecordProperty[Key, MetaValue]]
) extends NewMultiRecordProperty[Key, Value]
  with RichProperty[Key, Value, MetaValue, ImplNewRecordProperty[Key, MetaValue]]
