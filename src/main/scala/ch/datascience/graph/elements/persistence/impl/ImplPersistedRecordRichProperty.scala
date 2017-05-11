package ch.datascience.graph.elements.persistence.impl

import ch.datascience.graph.elements.persistence.{PersistedRecordRichProperty, RecordPath}
import ch.datascience.graph.elements.{BoxedOrValidValue, Properties}

/**
  * Created by johann on 11/05/17.
  */
case class ImplPersistedRecordRichProperty[+Key, +Value: BoxedOrValidValue, MetaKey, +MetaValue: BoxedOrValidValue](
  parent: RecordPath[Key],
  key: Key,
  value: Value,
  properties: Properties[MetaKey, MetaValue, ImplPersistedRecordProperty[MetaKey, MetaValue]]
) extends PersistedRecordRichProperty[Key, Value, MetaKey, MetaValue,ImplPersistedRecordProperty[MetaKey, MetaValue], ImplPersistedRecordRichProperty[Key, Value, MetaKey, MetaValue]]
