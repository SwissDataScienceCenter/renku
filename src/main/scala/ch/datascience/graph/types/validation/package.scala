package ch.datascience.graph.types

/**
  * Validation of graph types
  */
package object validation {

  type ValidationResult[C] = Either[ValidationError, C]

}
