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
+Prop <: RichProperty[Key, Value, MetaKey, MetaValue, MetaProp, Prop] with PersistedMultiRecordProperty[PropId, Key, Value, Prop] with HasPath[RecordPath[MetaKey]]
] extends Vertex[TypeId, Key, Value, MetaKey, MetaValue, MetaProp, Prop]
  with PersistedElement[VertexPath[Id] with MultiRecordPath[PropId]]
  with HasId[Id] {

  final def path: VertexPath[Id] with MultiRecordPath[PropId] = new VertexPath(id) with MultiRecordPath[PropId]

}

sealed trait PersistedProperty[+Key, +Value, +This <: PropertyBase[Key, Value]]
  extends Property[Key, Value, This]
    with PersistedElement[PropertyPath] { this: This =>
}

trait PersistedRecordProperty[+Key, +Value, +This <: PropertyBase[Key, Value]]
  extends PersistedProperty[Key, Value, This]
    with PersistedElement[PropertyPathWithKey[Key]] { this: This =>

  def parent: RecordPath[Key]

  def path: PropertyPathWithKey[Key] = PropertyPathWithKey(parent, key)

}

trait PersistedMultiRecordProperty[+Id, +Key, +Value, +This <: PropertyBase[Key, Value]]
  extends PersistedProperty[Key, Value, This]
    with PersistedElement[PropertyPathWithId[Id]]
    with HasId[Id] { this: This =>

  def parent: MultiRecordPath[Id]

  def path: PropertyPathWithId[Id] = PropertyPathWithId(parent, id)

}

trait PersistedRecordRichProperty[+Key, +Value, MetaKey, +MetaValue, +MetaProp <: PersistedRecordProperty[MetaKey, MetaValue, MetaProp],
+This <: RichPropertyBase[Key, Value, MetaKey, MetaValue, MetaProp]]
  extends PersistedProperty[Key, Value, This]
    with PersistedRecordProperty[Key, Value, This]
    with RichProperty[Key, Value, MetaKey, MetaValue, MetaProp, This]
    with PersistedElement[PropertyPathWithKey[Key] with RecordPath[MetaKey]] { this: This =>

  def parent: RecordPath[Key]

  override final def path: PropertyPathWithKey[Key] with RecordPath[MetaKey] = new PropertyPathWithKey(parent, key) with RecordPath[MetaKey]

}

trait PersistedMultiRecordRichProperty[+Id, +Key, +Value, MetaKey, +MetaValue, +MetaProp <: PersistedRecordProperty[MetaKey, MetaValue, MetaProp],
+This <: RichPropertyBase[Key, Value, MetaKey, MetaValue, MetaProp]]
  extends PersistedProperty[Key, Value, This]
    with PersistedMultiRecordProperty[Id, Key, Value, This]
    with RichProperty[Key, Value, MetaKey, MetaValue, MetaProp, This]
    with PersistedElement[PropertyPathWithId[Id] with RecordPath[MetaKey]] { this: This =>

  def parent: MultiRecordPath[Id]

  override final def path: PropertyPathWithId[Id] with RecordPath[MetaKey] = new PropertyPathWithId(parent, id) with RecordPath[MetaKey]

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
