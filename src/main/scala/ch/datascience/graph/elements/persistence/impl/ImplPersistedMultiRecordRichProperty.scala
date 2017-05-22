package ch.datascience.graph.elements.persistence.impl

import ch.datascience.graph.elements.Properties
import ch.datascience.graph.elements.persistence.{Path, PersistedMultiRecordRichProperty, PersistedRecordProperty}
import ch.datascience.graph.values.BoxedOrValidValue

/**
  * Created by johann on 11/05/17.
  */
case class ImplPersistedMultiRecordRichProperty[+Id, Key, +Value: BoxedOrValidValue, +MetaValue: BoxedOrValidValue](
  parent: Path,
  id: Id,
  key: Key,
  value: Value,
  properties: Properties[Key, MetaValue, PersistedRecordProperty[Key, MetaValue]]
) extends PersistedMultiRecordRichProperty[Id, Key, Value, MetaValue]
