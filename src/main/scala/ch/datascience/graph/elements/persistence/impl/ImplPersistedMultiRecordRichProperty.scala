package ch.datascience.graph.elements.persistence.impl

import ch.datascience.graph.elements.persistence.{Path, PersistedMultiRecordProperty}
import ch.datascience.graph.elements.{BoxedOrValidValue, Properties, RichProperty}

/**
  * Created by johann on 11/05/17.
  */
case class ImplPersistedMultiRecordRichProperty[+Id, +Key, +Value: BoxedOrValidValue, MetaKey, +MetaValue: BoxedOrValidValue](
  parent: Path,
  id: Id,
  key: Key,
  value: Value,
  properties: Properties[MetaKey, MetaValue, ImplPersistedRecordProperty[MetaKey, MetaValue]]
) extends PersistedMultiRecordProperty[Id, Key, Value, ImplPersistedMultiRecordRichProperty[Id, Key, Value, MetaKey, MetaValue]]
  with RichProperty[Key, Value, MetaKey, MetaValue, ImplPersistedRecordProperty[MetaKey, MetaValue], ImplPersistedMultiRecordRichProperty[Id, Key, Value, MetaKey, MetaValue]]
