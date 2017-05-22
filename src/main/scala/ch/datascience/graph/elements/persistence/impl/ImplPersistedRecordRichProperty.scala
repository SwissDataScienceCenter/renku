package ch.datascience.graph.elements.persistence.impl

import ch.datascience.graph.elements.Properties
import ch.datascience.graph.elements.persistence.{Path, PersistedRecordProperty, PersistedRecordRichProperty}
import ch.datascience.graph.values.BoxedOrValidValue

/**
  * Created by johann on 11/05/17.
  */
case class ImplPersistedRecordRichProperty[Key, +Value: BoxedOrValidValue, +MetaValue: BoxedOrValidValue, +MetaProp <: PersistedRecordProperty[Key, MetaValue]](
  parent: Path,
  key: Key,
  value: Value,
  properties: Properties[Key, MetaValue, MetaProp]
) extends PersistedRecordRichProperty[Key, Value, MetaValue, MetaProp]
