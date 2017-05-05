package ch.datascience.graph.typecheck

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
