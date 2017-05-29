package ch.datascience.graph.types.validation

import ch.datascience.graph.types.{PropertyKey, RecordType}

/**
  * Created by johann on 09/05/17.
  */
sealed trait Validated

trait ValidatedPropertyKey extends Validated {

  def propertyKey: PropertyKey

}

trait ValidatedRecordType extends Validated {

  def recordType: RecordType

  def propertyKeys: Map[PropertyKey#Key, PropertyKey]

}
