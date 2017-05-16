package ch.datascience.graph.types

import ch.datascience.graph.HasKey
import ch.datascience.graph.types.impl.{ImplNamedType, ImplRecordType}

/**
  * Base trait for graph types
  */
sealed trait GraphType {

  /**
    * Subtype check
    *
    * @param t the other type
    * @return true iff this is a subtype of t
    */
  def <<(t: GraphType): Boolean

  /**
    * Supertype check
    *
    * @param t the other type
    * @return true iff this is a supertype of t
    */
  def >>(t: GraphType): Boolean = t << this

}


/**
  * Bottom type
  *
  * There should be no instance of Bottom.
  */
case object Bottom extends GraphType {

  def <<(t: GraphType): Boolean = true

}

/**
  * Base trait for record types
  *
  * Only property keys are taken into account, not the properties themselves.
  *
  * @tparam PropKey type of property keys
  */
trait RecordType[PropKey] extends GraphType {

  def properties: Set[PropKey]

  final def <<(t: GraphType): Boolean = t match {
    case rt: RecordType[PropKey] => rt.properties subsetOf this.properties
    case _                       => false
  }

}

/**
  * Base trait for named types
  *
  * Like [[RecordType]], only keys are used for named record types and properties.
  *
  * @tparam TypeKey type of named type keys
  * @tparam PropKey type of property keys
  */
trait NamedType[TypeKey, PropKey] extends GraphType with HasKey[TypeKey] {

  def superTypes: Set[TypeKey]

  def properties: Set[PropKey]

  def like: RecordType[PropKey] = RecordType(properties)

  final def <<(t: GraphType): Boolean = t match {
    case nt: NamedType[TypeKey, PropKey] => (superTypes + this.key) contains nt.key
    case rt: RecordType[PropKey]         => rt.properties subsetOf this.properties
    case _                               => false
  }

  def simpleCopy: NamedType[TypeKey, PropKey] = NamedType(key, superTypes, properties)

}


object RecordType {

  def apply[PropKey](properties: Set[PropKey]): RecordType[PropKey] = ImplRecordType(properties)

  def unapply[PropKey](recordType: RecordType[PropKey]): Option[Set[PropKey]] = {
    if (recordType eq null)
      None
    else
      Some(recordType.properties)
  }

}

object NamedType {

  def apply[TypeKey, PropKey](key: TypeKey, superTypes: Set[TypeKey], properties: Set[PropKey]): NamedType[TypeKey, PropKey] = ImplNamedType(key, superTypes, properties)

  def unapply[TypeKey, PropKey](namedType: NamedType[TypeKey, PropKey]): Option[(TypeKey, Set[TypeKey], Set[PropKey])] = {
    if (namedType eq null)
      None
    else
      Some(namedType.key, namedType.superTypes, namedType.properties)
  }

}
