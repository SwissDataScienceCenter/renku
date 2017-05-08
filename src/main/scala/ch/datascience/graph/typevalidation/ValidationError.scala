package ch.datascience.graph.typevalidation

import ch.datascience.graph.elements.Property
import ch.datascience.graph.types.DataType

/**
  * Created by johann on 01/05/17.
  */
sealed trait ValidationError {

}

final case class MultipleErrors(errors: Seq[ValidationError]) extends ValidationError

final case class UnknownProperty[+Key](key: Key) extends ValidationError

final case class BadDataType[+Key, +Value, +Prop <: Property[Key, Value, Prop]](
  value   : Value,
  required: DataType,
  found   : DataType
) extends ValidationError

final case class BadRecord[+Key, +Value, +Prop <: Property[Key, Value, Prop]](
  required: Key,
  found: Key
) extends ValidationError


object MultipleErrors {

  def make(errors: Seq[ValidationError]): ValidationError = errors.size match {
    case 1 => errors.head
    case _ => new MultipleErrors(errors)
  }

}
