package ch.datascience.graph.elements.persisted.impl

import ch.datascience.graph.elements.persisted.{Path, PersistedMultiRecordRichProperty, PersistedRecordProperty}
import ch.datascience.graph.values.BoxedOrValidValue

/**
  * Created by johann on 11/05/17.
  */
case class ImplPersistedMultiRecordRichProperty[I](
  parent: Path,
  id: I,
  key: PersistedMultiRecordRichProperty#Key,
  value: PersistedMultiRecordRichProperty#Value,
  properties: Map[PersistedMultiRecordRichProperty#Prop#Key, PersistedMultiRecordRichProperty#Prop]
) extends PersistedMultiRecordRichProperty {

  type Id = I

}
