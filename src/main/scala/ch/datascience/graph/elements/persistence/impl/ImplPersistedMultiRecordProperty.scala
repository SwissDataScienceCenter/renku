package ch.datascience.graph.elements.persistence.impl

import ch.datascience.graph.elements.BoxedOrValidValue
import ch.datascience.graph.elements.persistence.{Path, PersistedMultiRecordProperty}

/**
  * Created by johann on 11/05/17.
  */
case class ImplPersistedMultiRecordProperty[+Id, +Key, +Value: BoxedOrValidValue](
  parent: Path,
  id: Id,
  key: Key,
  value: Value
) extends PersistedMultiRecordProperty[Id, Key, Value, ImplPersistedMultiRecordProperty[Id, Key, Value]]
