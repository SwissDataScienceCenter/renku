package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.MultiProperties
import ch.datascience.graph.elements.persistence.NewVertex
import ch.datascience.graph.elements.persistence.impl.ImplPersistedMultiRecordRichProperty
import ch.datascience.graph.values.BoxedValue

/**
  * Created by jeberle on 15.05.17.
  */
case class ImplNewVertex[
TypeId,
Key,
+Value
](
   tempId: Int,
   types: Set[TypeId],
   properties: MultiProperties[Key, Value, ImplNewMultiRecordRichProperty[Key, Value, BoxedValue]]
 ) extends NewVertex[
  TypeId,
  Key,
  Value,
  BoxedValue,
  ImplNewRecordProperty[Key, BoxedValue],
  ImplNewMultiRecordRichProperty[Key, Value, BoxedValue]
  ]
