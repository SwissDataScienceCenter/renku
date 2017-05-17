package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.persistence.{NewRecordProperty, Path}
import ch.datascience.graph.elements.{BoxedOrValidValue, Properties, RichProperty}

/**
  * Created by johann on 11/05/17.
  */
case class ImplNewRecordRichProperty[+Key, +Value: BoxedOrValidValue, MetaKey, +MetaValue: BoxedOrValidValue](
  parent: Path,
  key: Key,
  value: Value,
  properties: Properties[MetaKey, MetaValue, ImplNewRecordProperty[MetaKey, MetaValue]]
) extends NewRecordProperty[Key, Value, ImplNewRecordRichProperty[Key, Value, MetaKey, MetaValue]]
  with RichProperty[Key, Value, MetaKey, MetaValue, ImplNewRecordProperty[MetaKey, MetaValue], ImplNewRecordRichProperty[Key, Value, MetaKey, MetaValue]]
