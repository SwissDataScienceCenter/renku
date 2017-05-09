package ch.datascience.graph.types.validation

/**
  * Created by johann on 09/05/17.
  */
sealed trait Validated

trait ValidatedPropertyKey[+Key] extends Validated {



}
