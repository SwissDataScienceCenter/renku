package ch.datascience.graph.types.validation

import ch.datascience.graph.types.{PropertyKey, RecordType}

/**
  * Created by johann on 09/05/17.
  */
sealed trait Validated

trait ValidatedPropertyKey[+Key] extends Validated {

  def propertyKey: PropertyKey[Key]

}

trait ValidatedRecordType[Key] extends Validated {

  def recordType: RecordType[Key]

  def propertyKeys: Map[Key, PropertyKey[Key]]

}
