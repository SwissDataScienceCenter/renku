package ch.datascience.graph.types.validation

/**
  * Created by johann on 09/05/17.
  */
sealed trait ValidationError

final case class MultipleErrors(errors: Seq[ValidationError]) extends ValidationError

final case class UnknownProperty[+Key](key: Key) extends ValidationError

object MultipleErrors {

  def make(errors: Seq[ValidationError]): ValidationError = errors.size match {
    case 1 => errors.head
    case _ => new MultipleErrors(errors)
  }

}
