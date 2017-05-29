package ch.datascience.graph.elements.persisted.impl

import ch.datascience.graph.elements.persisted.{Path, PersistedRecordProperty, PersistedRecordRichProperty}
import ch.datascience.graph.values.BoxedOrValidValue

/**
  * Created by johann on 11/05/17.
  */
//MetaProp <: PersistedRecordProperty[Key, MetaValue]
case class ImplPersistedRecordRichProperty(
  parent: Path,
  key: PersistedRecordRichProperty#Key,
  value: PersistedRecordRichProperty#Value,
  properties: Map[PersistedRecordRichProperty#Prop#Key, PersistedRecordRichProperty#Prop]
) extends PersistedRecordRichProperty {

}
