package ch.datascience.graph

/**
  * Created by johann on 01/05/17.
  */
package object typecheck {

  type ValidationResult[C] = Either[ValidationError, C]

}
