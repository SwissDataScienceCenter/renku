package ch.datascience.graph.elements.persistence.impl

import ch.datascience.graph.elements.persistence.{Path, PersistedRecordProperty}
import ch.datascience.graph.values.BoxedOrValidValue

/**
  * Created by johann on 11/05/17.
  */
case class ImplPersistedRecordProperty[+Key, +Value: BoxedOrValidValue](
  parent: Path,
  key: Key,
  value: Value
) extends PersistedRecordProperty[Key, Value]
