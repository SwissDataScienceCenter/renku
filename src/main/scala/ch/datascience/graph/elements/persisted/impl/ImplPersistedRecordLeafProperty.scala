package ch.datascience.graph.elements.persisted.impl

import ch.datascience.graph.elements.persisted.{Path, PersistedRecordProperty}

/**
  * Created by johann on 11/05/17.
  */
private[persisted] case class ImplPersistedRecordLeafProperty(
  parent: Path,
  key: PersistedRecordProperty#Key,
  value: PersistedRecordProperty#Value
) extends PersistedRecordProperty
