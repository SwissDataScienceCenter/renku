package ch.datascience.graph.elements.validation

import ch.datascience.graph.elements._
import ch.datascience.graph.types.{NamedType, PropertyKey, RecordType}

sealed trait Validated

trait ValidatedProperty[+Key, +Value, +Prop <: Property[Key, Value]] extends Validated {

  /**
    * The validated property
    * @return property
    */
  def property: Prop

  /**
    * The definition of the validated property
    *
    * @return property key
    */
  def propertyKey: PropertyKey[Key]

}

trait ValidatedMultiProperty[+Key, +Value, +Prop <: Property[Key, Value]] extends Validated {

  /**
    * The validated property
    * @return property
    */
  def properties: MultiPropertyValue[Key, Value, Prop]

  /**
    * The definition of the validated property
    *
    * @return property key
    */
  def propertyKey: PropertyKey[Key]

}

trait ValidatedRecord[Key, +Value, +Prop <: Property[Key, Value]] extends Validated {

  /**
    * The validated record
    * @return
    */
  def record: Record[Key, Value, Prop]

  /**
    * The record type of the validated record
    * @return
    */
  def recordType: RecordType[Key]

  /**
    * The definitions of the validated properties
    *
    * @return property key map
    */
  def propertyKeys: Map[Key, PropertyKey[Key]]

}

trait ValidatedMultiRecord[Key, +Value, +Prop <: Property[Key, Value]] extends Validated {

  /**
    * The validated record
    * @return
    */
  def record: MultiRecord[Key, Value, Prop]

  /**
    * The record type of the validated record
    * @return
    */
  def recordType: RecordType[Key]

  /**
    * The definitions of the validated properties
    *
    * @return property key map
    */
  def propertyKeys: Map[Key, PropertyKey[Key]]

}

trait ValidatedTypedRecord[TypeId, Key, +Value, +Prop <: Property[Key, Value]] extends Validated {

  /**
    * The validated record
    * @return
    */
  def record: TypedRecord[TypeId, Key, Value, Prop]

  /**
    * The definitions of the validated named types
    *
    * @return named type map
    */
  def namedTypes: Map[TypeId, NamedType[TypeId, Key]]

  /**
    * The record type of the validated record
    * @return
    */
  def recordType: RecordType[Key]

  /**
    * The definitions of the validated properties
    *
    * @return property key map
    */
  def propertyKeys: Map[Key, PropertyKey[Key]]

}

trait ValidatedTypedMultiRecord[TypeId, Key, +Value, +Prop <: Property[Key, Value]] extends Validated {

  /**
    * The validated record
    * @return
    */
  def record: TypedMultiRecord[TypeId, Key, Value, Prop]

  /**
    * The definitions of the validated named types
    *
    * @return named type map
    */
  def namedTypes: Map[TypeId, NamedType[TypeId, Key]]

  /**
    * The record type of the validated record
    * @return
    */
  def recordType: RecordType[Key]

  /**
    * The definitions of the validated properties
    *
    * @return property key map
    */
  def propertyKeys: Map[Key, PropertyKey[Key]]

}

trait ValidatedVertex[
TypeId,
Key,
+Value,
+MetaValue,
+MetaProp <: Property[Key, MetaValue],
+Prop <: RichProperty[Key, Value, MetaValue, MetaProp]
] extends Validated {

  /**
    * The validated vertex
    * @return
    */
  def vertex: Vertex[TypeId, Key, Value, MetaValue, MetaProp, Prop]

  /**
    * The definitions of the validated named types
    *
    * @return named type map
    */
  def namedTypes: Map[TypeId, NamedType[TypeId, Key]]

  /**
    * The record type of the validated record
    * @return
    */
  def recordType: RecordType[Key]

  /**
    * The definitions of the validated properties
    *
    * @return property key map
    */
  def propertyKeys: Map[Key, PropertyKey[Key]]

  /**
    * The definitions of the validated meta-properties
    */
  def metaPropertyKeys: Map[Key, PropertyKey[Key]]

}
