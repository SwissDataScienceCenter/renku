package ch.datascience.graph

/**
  * Created by johann on 01/05/17.
  */
package object typevalidation {

  type ValidationResult[C] = Either[ValidationError, C]

}
