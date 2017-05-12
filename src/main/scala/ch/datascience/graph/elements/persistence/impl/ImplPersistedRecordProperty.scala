package ch.datascience.graph.elements.persistence.impl

import ch.datascience.graph.elements.BoxedOrValidValue
import ch.datascience.graph.elements.persistence.{PersistedRecordProperty, RecordPath}

/**
  * Created by johann on 11/05/17.
  */
case class ImplPersistedRecordProperty[+Key, +Value: BoxedOrValidValue](
  parent: RecordPath,
  key: Key,
  value: Value
) extends PersistedRecordProperty[Key, Value, ImplPersistedRecordProperty[Key, Value]]
