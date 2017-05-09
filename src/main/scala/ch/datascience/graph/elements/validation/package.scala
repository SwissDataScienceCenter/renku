package ch.datascience.graph.elements

/**
  * Validation of graph elements
  */
package object validation {

  type ValidationResult[C] = Either[ValidationError, C]

}
