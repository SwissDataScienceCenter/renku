package ch.datascience.graph.elements.validation

import ch.datascience.graph.elements.{MultiRecord, Property, Record}
import ch.datascience.graph.types._

/**
  * Created by johann on 01/05/17.
  */
sealed trait ValidationError

final case class MultipleErrors(errors: Seq[ValidationError]) extends ValidationError

final case class UnknownProperty(key: PropertyKey#Key) extends ValidationError

final case class UnknownType(typeId: NamedType#TypeId) extends ValidationError

final case class WrongDefinition(
  required: PropertyKey#Key,
  found: PropertyKey#Key
) extends ValidationError

final case class BadDataType(
  key     : PropertyKey#Key,
  required: DataType,
  found   : DataType
) extends ValidationError

final case class BadCardinality(
  key: PropertyKey#Key,
  required: Cardinality,
  found: Cardinality
) extends ValidationError

final case class BadRecord(
  required: PropertyKey#Key,
  found: PropertyKey#Key
) extends ValidationError

final case class RecordTypeError(
  record: Record,
  required: RecordType,
  missing: Set[RecordType#Key] // keys missing from record type check
) extends ValidationError

final case class MultiRecordTypeError(
  record: MultiRecord,
  required: RecordType,
  missing: Set[RecordType#Key] // keys missing from record type check
) extends ValidationError

object MultipleErrors {

  def make(errors: Seq[ValidationError]): ValidationError = errors.size match {
    case 1 => errors.head
    case _ => new MultipleErrors(errors)
  }

}
