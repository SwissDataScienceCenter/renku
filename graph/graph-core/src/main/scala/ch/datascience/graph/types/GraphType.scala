package ch.datascience.graph.types

import ch.datascience.graph.bases.HasKey
import ch.datascience.graph.types.impl.{ImplNamedType, ImplRecordType}
import ch.datascience.graph.Constants

/**
  * Base trait for graph types
  */
sealed trait GraphType { self =>

//  type TypeId
//
//  type Key

  /**
    * Subtype check
    *
    * @param t the other type
    * @return true iff this is a subtype of t
    */
//  def <<(t: GraphType { type TypeId = self.TypeId; type Key = self.Key }): Boolean
  def <<(t: GraphType): Boolean

  /**
    * Supertype check
    *
    * @param t the other type
    * @return true iff this is a supertype of t
    */
  final def >>(t: GraphType): Boolean = t << this

}


/**
  * Bottom type
  *
  * There should be no instance of Bottom.
  */
//trait Bottom extends GraphType { self =>
case object Bottom extends GraphType {

//  type TypeId = Nothing
//
//  type Key = Nothing

//  def <<(t: GraphType { type TypeId = self.TypeId; type Key = self.Key }): Boolean = true
  def <<(t: GraphType): Boolean = true

}

///**
//  * Base trait for record types
//  *
//  * Only property keys are taken into account, not the properties themselves.
//  *
//  * @tparam PropKey type of property keys
//  */
//trait RecordType[PropKey] extends GraphType {
trait RecordType extends GraphType { self =>

  final type Key = Constants.Key

  def properties: Set[Key]

  final def <<(t: GraphType): Boolean = t match {
    case RecordType(props) => props subsetOf properties
    case _ => false
  }

}

///**
//  * Base trait for named types
//  *
//  * Like [[RecordType]], only keys are used for named record types and properties.
//  *
//  * @tparam TypeKey type of named type keys
//  * @tparam PropKey type of property keys
//  */
//trait NamedType[TypeKey, PropKey] extends GraphType with HasKey[TypeKey] {
trait NamedType extends GraphType { self =>

  final type TypeId = Constants.TypeId

  final type Key = Constants.Key

  def typeId: TypeId

  def superTypes: Set[TypeId]

  def properties: Set[Key]

  def like: RecordType = RecordType(properties)

  final def <<(t: GraphType): Boolean = t match {
    case NamedType(tid, _, _) => (superTypes + this.typeId) contains tid
    case _ => this.like << t
  }

}


//object Bottom {
//
//  def apply[T, K]: Bottom { type TypeId = T; type Key = K } = ImplBottom[T, K]()
//
//}

object RecordType {

  def apply(properties: Set[RecordType#Key]): RecordType = ImplRecordType(properties)

  def unapply(recordType: RecordType): Option[Set[RecordType#Key]] = {
    if (recordType eq null)
      None
    else
      Some(recordType.properties)
  }

}

object NamedType {

  def apply(typeId: NamedType#TypeId, superTypes: Set[NamedType#TypeId], properties: Set[NamedType#Key]): NamedType = ImplNamedType(typeId, superTypes, properties)

  def unapply(namedType: NamedType): Option[(NamedType#TypeId, Set[NamedType#TypeId], Set[NamedType#Key])] = {
    if (namedType eq null)
      None
    else
      Some(namedType.typeId, namedType.superTypes, namedType.properties)
  }

}
