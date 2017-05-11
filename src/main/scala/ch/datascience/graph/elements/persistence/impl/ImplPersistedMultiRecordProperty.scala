package ch.datascience.graph.elements.persistence.impl

import ch.datascience.graph.elements.BoxedOrValidValue
import ch.datascience.graph.elements.persistence.{MultiRecordPath, PersistedMultiRecordProperty}

/**
  * Created by johann on 11/05/17.
  */
case class ImplPersistedMultiRecordProperty[+Id, +Key, +Value: BoxedOrValidValue](
  parent: MultiRecordPath[Id],
  id: Id,
  key: Key,
  value: Value
) extends PersistedMultiRecordProperty[Id, Key, Value, ImplPersistedMultiRecordProperty[Id, Key, Value]]
