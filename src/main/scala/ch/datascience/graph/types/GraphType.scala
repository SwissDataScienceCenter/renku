package ch.datascience.graph.types

import ch.datascience.graph.HasKey

/**
  * Base trait for graph types
  */
sealed trait GraphType {

  /**
    * Subtype check
    * @param t the other type
    * @return true iff this is a subtype of t
    */
  def <<(t: GraphType): Boolean
  def >>(t: GraphType): Boolean = t << this

}


/**
  * Bottom type
  *
  * There should be no instance of Bottom.
  */
case object Bottom extends GraphType {

  def <<(t: GraphType): Boolean = t match {
    case Bottom => true
    case _ => false
  }

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
    case _ => false
  }

}

/**
  * Base trait for named record types
  *
  * Like [[RecordType]], only keys are used for named record types and properties.
  *
  * @tparam TypeKey type of named record type keys
  * @tparam PropKey type of property keys
  */
trait NamedRecordType[TypeKey, PropKey] extends GraphType with HasKey[TypeKey] {

  def superTypes: Set[TypeKey]

  def properties: Set[PropKey]

  def like: RecordType[PropKey]

  final def <<(t: GraphType): Boolean = t match {
    case nrt: NamedRecordType[TypeKey, PropKey] => (superTypes + this.key) contains nrt.key
    case rt: RecordType[PropKey] => rt.properties subsetOf this.properties
    case _ => false
  }
}

