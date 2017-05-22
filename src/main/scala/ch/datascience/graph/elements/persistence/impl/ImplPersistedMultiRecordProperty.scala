package ch.datascience.graph.elements.persistence.impl

import ch.datascience.graph.elements.persistence.{Path, PersistedMultiRecordProperty}
import ch.datascience.graph.values.BoxedOrValidValue

/**
  * Created by johann on 11/05/17.
  */
case class ImplPersistedMultiRecordProperty[+Id, +Key, +Value: BoxedOrValidValue](
  parent: Path,
  id: Id,
  key: Key,
  value: Value
) extends PersistedMultiRecordProperty[Id, Key, Value]
