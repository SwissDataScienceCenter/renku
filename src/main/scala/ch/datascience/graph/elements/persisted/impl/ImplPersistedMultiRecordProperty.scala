package ch.datascience.graph.elements.persisted.impl

import ch.datascience.graph.elements.persisted.{Path, PersistedMultiRecordProperty}

/**
  * Created by johann on 11/05/17.
  */
case class ImplPersistedMultiRecordProperty[I](
  parent: Path,
  id: I,
  key: PersistedMultiRecordProperty#Key,
  value: PersistedMultiRecordProperty#Value
) extends PersistedMultiRecordProperty {

  type Id = I

}
