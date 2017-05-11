package ch.datascience.graph.elements.persistence.impl

import ch.datascience.graph.elements.{BoxedValue, MultiProperties, RichProperty}
import ch.datascience.graph.elements.persistence._

/**
  * Created by johann on 11/05/17.
  */
case class ImplPersistedVertex[
+Id,
TypeId,
Key,
+Value,
MetaKey,
+PropId
](
  id: Id,
  types: Set[TypeId],
  properties: MultiProperties[Key, Value, ImplPersistedMultiRecordRichProperty[PropId, Key, Value, MetaKey, BoxedValue]]
) extends PersistedVertex[Id, TypeId, Key, Value, MetaKey, BoxedValue, ImplPersistedRecordProperty[MetaKey, BoxedValue], PropId, ImplPersistedMultiRecordRichProperty[PropId, Key, Value, MetaKey, BoxedValue]]
