package ch.datascience.graph.elements.persistence.impl

import ch.datascience.graph.elements.persistence._
import ch.datascience.graph.elements.MultiProperties

/**
  * Created by johann on 11/05/17.
  */
case class ImplPersistedVertex[
+Id,
TypeId,
Key,
+Value,
MetaKey,
+MetaValue,
+PropId
](
  id: Id,
  types: Set[TypeId],
  properties: MultiProperties[Key, Value, ImplPersistedMultiRecordRichProperty[PropId, Key, Value, MetaKey, MetaValue]]
) extends PersistedVertex[Id, TypeId, Key, Value, MetaKey, MetaValue, ImplPersistedRecordProperty[MetaKey, MetaValue], PropId, ImplPersistedMultiRecordRichProperty[PropId, Key, Value, MetaKey, MetaValue]]
