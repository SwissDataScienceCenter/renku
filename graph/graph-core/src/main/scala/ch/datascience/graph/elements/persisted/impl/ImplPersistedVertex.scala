package ch.datascience.graph.elements.persisted.impl

import ch.datascience.graph.elements.persisted.PersistedVertex

private[persisted] case class ImplPersistedVertex(
  id: PersistedVertex#Id,
  types: Set[PersistedVertex#TypeId],
  properties: PersistedVertex#Properties
) extends PersistedVertex

///**
//  * Created by johann on 11/05/17.
//  */
//case class ImplPersistedVertex[
//+Id,
//TypeId,
//Key,
//+Value,
//+MetaValue,
//+MetaProp <: PersistedRecordProperty[Key, MetaValue],
//+PropId
//](
//  id: Id,
//  types: Set[TypeId],
//  properties: MultiProperties[Key, Value, PersistedMultiRecordRichProperty[PropId, Key, Value, MetaValue, MetaProp]]
//) extends PersistedVertex[Id, TypeId, Key, Value, MetaValue, PersistedRecordProperty[Key, MetaValue], PropId, PersistedMultiRecordRichProperty[PropId, Key, Value, MetaValue, MetaProp]]
