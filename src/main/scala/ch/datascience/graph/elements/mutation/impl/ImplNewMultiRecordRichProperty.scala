package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.persistence.{NewMultiRecordProperty, Path}
import ch.datascience.graph.elements.{BoxedOrValidValue, Properties, RichProperty}

/**
  * Created by johann on 11/05/17.
  */
case class ImplNewMultiRecordRichProperty[+Key, +Value: BoxedOrValidValue, MetaKey, +MetaValue: BoxedOrValidValue](
  parent: Path,
  tempId: Int,
  key: Key,
  value: Value,
  properties: Properties[MetaKey, MetaValue, ImplNewRecordProperty[MetaKey, MetaValue]]
) extends NewMultiRecordProperty[Key, Value, ImplNewMultiRecordRichProperty[Key, Value, MetaKey, MetaValue]]
  with RichProperty[Key, Value, MetaKey, MetaValue, ImplNewRecordProperty[MetaKey, MetaValue], ImplNewMultiRecordRichProperty[Key, Value, MetaKey, MetaValue]]
