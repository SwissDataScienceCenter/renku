package ch.datascience.graph.elements.persistence.impl

import ch.datascience.graph.elements.persistence.{Path, PersistedRecordProperty}
import ch.datascience.graph.elements.{BoxedOrValidValue, Properties, RichProperty}

/**
  * Created by johann on 11/05/17.
  */
case class ImplPersistedRecordRichProperty[+Key, +Value: BoxedOrValidValue, MetaKey, +MetaValue: BoxedOrValidValue](
  parent: Path,
  key: Key,
  value: Value,
  properties: Properties[MetaKey, MetaValue, ImplPersistedRecordProperty[MetaKey, MetaValue]]
) extends PersistedRecordProperty[Key, Value, ImplPersistedRecordRichProperty[Key, Value, MetaKey, MetaValue]]
  with RichProperty[Key, Value, MetaKey, MetaValue, ImplPersistedRecordProperty[MetaKey, MetaValue], ImplPersistedRecordRichProperty[Key, Value, MetaKey, MetaValue]]
