//package ch.datascience.graph.elements.persistence
//
//import ch.datascience.graph.bases.HasId
//import ch.datascience.graph.elements._
//import ch.datascience.graph.elements.persisted._
//import ch.datascience.graph.elements.persisted.impl.ImplPersistedRecordProperty
//
///**
//  * Created by johann on 11/05/17.
//  */
//sealed trait NewOrPersistedElement extends Element
//
//sealed trait PersistedElement[+P <: Path] extends NewOrPersistedElement with HasPath[P]
//
//sealed trait NewElement extends NewOrPersistedElement
//
//trait PersistedVertex[
//+Id,
//TypeId,
//Key,
//+Value,
//+MetaValue,
//+MetaProp <: PersistedRecordProperty[Key, MetaValue],
//+PropId,
//+Prop <: PersistedMultiRecordRichProperty[PropId, Key, Value, MetaValue, MetaProp]
//] extends Vertex[TypeId, Key, Value, MetaValue, MetaProp, Prop]
//  with PersistedElement[VertexPath[Id]]
//  with HasId[Id] {
//
//  final def path: VertexPath[Id] = VertexPath(id)
//
//}
//
//trait PersistedEdge[
//+Id,
//Key,
//+Value,
//+EdgeProp <: PersistedRecordProperty[Key, Value],
//+VertexId
//] extends Edge[Key, Value, EdgeProp, VertexId]
//  with PersistedElement[EdgePath[VertexId, Id]]
//  with HasId[Id]{
//
//  final def path: EdgePath[VertexId, Id] = EdgePath(from, id)
//
//}
//
//sealed trait PersistedProperty[+Key, +Value]
//  extends Property[Key, Value]
//    with PersistedElement[PropertyPath] {
//
//  def parent: Path
//
//}
//
//trait PersistedRecordProperty[+Key, +Value]
//  extends PersistedProperty[Key, Value]
//    with PersistedElement[PropertyPathFromRecord[Key]] {
//
//  final def path: PropertyPathFromRecord[Key] = PropertyPathFromRecord(parent, key)
//
//}
//
//trait PersistedMultiRecordProperty[+Id, +Key, +Value]
//  extends PersistedProperty[Key, Value]
//    with PersistedElement[PropertyPathFromMultiRecord[Id]]
//    with HasId[Id] {
//
//  final def path: PropertyPathFromMultiRecord[Id] = PropertyPathFromMultiRecord(parent, id)
//
//}
//
//trait PersistedRecordRichProperty[Key, +Value, +MetaValue, +MetaProp <: PersistedRecordProperty[Key, MetaValue]]
//  extends PersistedRecordProperty[Key, Value]
//    with RichProperty[Key, Value, MetaValue, MetaProp]
//
//trait PersistedMultiRecordRichProperty[+Id, Key, +Value, +MetaValue, +MetaProp <: PersistedRecordProperty[Key, MetaValue]]
//  extends PersistedMultiRecordProperty[Id, Key, Value]
//    with RichProperty[Key, Value, MetaValue, MetaProp]
//
//
//sealed trait NewProperty[+Key, +Value]
//  extends Property[Key, Value]
//    with NewElement
//
//trait NewRecordProperty[+Key, +Value]
//  extends NewProperty[Key, Value]
//    with HasPath[Path]
//    with NewElement {
//
//  def parent: Path
//
//  final def path: PropertyPathFromRecord[Key] = PropertyPathFromRecord(parent, key)
//
//}
//
//trait NewMultiRecordProperty[+Key, +Value]
//  extends NewProperty[Key, Value]
//    with HasPath[Path]
//    with NewElement {
//
//  type TempId = Int
//
//  def tempId: TempId
//
//  def parent: Path
//
//  final def path: PropertyPathFromMultiRecord[TempId] = PropertyPathFromMultiRecord(parent, tempId)
//
//}
//
//
//trait NewVertex[
//TypeId,
//Key,
//+Value,
//+MetaValue,
//+MetaProp <: Property[Key, MetaValue],
//+Prop <: RichProperty[Key, Value, MetaValue, MetaProp]
//] extends Vertex[TypeId, Key, Value, MetaValue, MetaProp, Prop]
//  with NewElement {
//
//  type TempId = Int
//
//  def tempId: TempId
//
//}
//
//trait NewEdge[
//+Id,
//Key,
//+Value,
//+EdgeProp <: Property[Key, Value]
//] extends Edge[Key, Value, EdgeProp, Either[Id, NewVertex[Nothing, Nothing, Nothing, Nothing, Nothing, Nothing]#TempId]]
//  with NewElement {
//
//  type TempId = Int
//
//  def tempId: TempId
//}
