package ch.datascience.graph.elements.persistence

import ch.datascience.graph.elements._

/**
  * Created by johann on 11/05/17.
  */
sealed trait NewOrPersistedElement extends Element

sealed trait PersistedElement[+P <: Path] extends NewOrPersistedElement with HasPath[P]

sealed trait NewElement extends NewOrPersistedElement

trait PersistedVertex[
+Id,
TypeId,
Key,
+Value,
MetaKey,
+MetaValue,
+MetaProp <: PersistedRecordProperty[MetaKey, MetaValue, MetaProp],
+PropId,
+Prop <: RichProperty[Key, Value, MetaKey, MetaValue, MetaProp, Prop] with PersistedMultiRecordProperty[PropId, Key, Value, Prop]
] extends Vertex[TypeId, Key, Value, MetaKey, MetaValue, MetaProp, Prop]
  with PersistedElement[VertexPath[Id]]
  with HasId[Id] {

  final def path: VertexPath[Id] = VertexPath(id)

}

sealed trait PersistedProperty[+Key, +Value, +This <: PropertyBase[Key, Value]]
  extends Property[Key, Value, This]
    with PersistedElement[PropertyPath] { this: This =>
}

trait PersistedRecordProperty[+Key, +Value, +This <: PropertyBase[Key, Value]]
  extends PersistedProperty[Key, Value, This]
    with PersistedElement[PropertyPathFromRecord[Key]] { this: This =>

  def parent: RecordPath

  def path: PropertyPathFromRecord[Key] = PropertyPathFromRecord(parent, key)

}

trait PersistedMultiRecordProperty[+Id, +Key, +Value, +This <: PropertyBase[Key, Value]]
  extends PersistedProperty[Key, Value, This]
    with PersistedElement[PropertyPathFromMultiRecord[Id]]
    with HasId[Id] { this: This =>

  def parent: MultiRecordPath

  def path: PropertyPathFromMultiRecord[Id] = PropertyPathFromMultiRecord(parent, id)

}

trait PersistedRichRecordProperty[+Key, +Value, +This <: PropertyBase[Key, Value]]
  extends PersistedRecordProperty[Key, Value, This]
    with PersistedElement[RichPropertyPathFromRecord[Key]] { this: This =>

  def parent: RecordPath

  final override def path: RichPropertyPathFromRecord[Key] = new RichPropertyPathFromRecord(parent, key)

}

trait PersistedRichMultiRecordProperty[+Id, +Key, +Value, +This <: PropertyBase[Key, Value]]
  extends PersistedMultiRecordProperty[Id, Key, Value, This]
    with PersistedElement[RichPropertyPathFromMultiRecord[Id]] { this: This =>

  def parent: MultiRecordPath

  final override def path: RichPropertyPathFromMultiRecord[Id] = new RichPropertyPathFromMultiRecord(parent, id)

}

trait NewVertex[
TypeId,
Key,
+Value,
MetaKey,
+MetaValue,
+MetaProp <: Property[MetaKey, MetaValue, MetaProp],
+Prop <: RichProperty[Key, Value, MetaKey, MetaValue, MetaProp, Prop]
] extends Vertex[TypeId, Key, Value, MetaKey, MetaValue, MetaProp, Prop]
  with NewElement {

  type TempId = Int

  def tempId: TempId

}

// TODO: New elements
