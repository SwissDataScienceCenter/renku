package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.{BoxedValue, MultiProperties}
import ch.datascience.graph.elements.persistence.NewVertex
import ch.datascience.graph.elements.persistence.impl.ImplPersistedMultiRecordRichProperty

/**
  * Created by jeberle on 15.05.17.
  */
case class ImplNewVertex[
TypeId,
Key,
+Value,
MetaKey
](
   tempId: Int,
   types: Set[TypeId],
   properties: MultiProperties[Key, Value, ImplNewMultiRecordRichProperty[Key, Value, MetaKey, BoxedValue]]
 ) extends NewVertex[
  TypeId,
  Key,
  Value,
  MetaKey,
  BoxedValue,
  ImplNewRecordProperty[MetaKey, BoxedValue],
  ImplNewMultiRecordRichProperty[Key, Value, MetaKey, BoxedValue]
  ]
