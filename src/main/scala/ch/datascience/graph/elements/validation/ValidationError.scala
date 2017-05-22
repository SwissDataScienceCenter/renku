package ch.datascience.graph.elements.validation

import ch.datascience.graph.elements.{MultiRecord, Property, Record}
import ch.datascience.graph.types.{Cardinality, DataType, RecordType}

/**
  * Created by johann on 01/05/17.
  */
sealed trait ValidationError

final case class MultipleErrors(errors: Seq[ValidationError]) extends ValidationError

final case class UnknownProperty[+Key](key: Key) extends ValidationError

final case class UnknownType[+TypeId](key: TypeId) extends ValidationError

final case class WrongDefinition[+Key](
  required: Key,
  found: Key
) extends ValidationError

final case class BadDataType[+Key](
  key     : Key,
  required: DataType,
  found   : DataType
) extends ValidationError

final case class BadCardinality(
  required: Cardinality,
  found: Cardinality
) extends ValidationError

final case class BadRecord[+Key](
  required: Key,
  found: Key
) extends ValidationError

final case class RecordTypeError[Key, +Value, +Prop <: Property[Key, Value]](
  record: Record[Key, Value, Prop],
  required: RecordType[Key],
  missing: Set[Key] // keys missing from record type check
) extends ValidationError

final case class MultiRecordTypeError[Key, +Value, +Prop <: Property[Key, Value]](
  record: MultiRecord[Key, Value, Prop],
  required: RecordType[Key],
  missing: Set[Key] // keys missing from record type check
) extends ValidationError

object MultipleErrors {

  def make(errors: Seq[ValidationError]): ValidationError = errors.size match {
    case 1 => errors.head
    case _ => new MultipleErrors(errors)
  }

}
