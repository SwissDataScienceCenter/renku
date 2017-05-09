package ch.datascience.graph.types.validation

import ch.datascience.graph.scope.PropertyScope
import ch.datascience.graph.types.PropertyKey

import scala.concurrent.Future

/**
  * Created by johann on 09/05/17.
  */
trait PropertyKeyValidator[Key] {

  def validatePropertyKey(propertyKey: PropertyKey[Key]): Future[ValidationResult[ValidatedPropertyKey[Key]]] = {
    Future.successful( validatePropertyKeySync(propertyKey) )
  }

  def validatePropertyKeySync(propertyKey: PropertyKey[Key]): ValidationResult[ValidatedPropertyKey[Key]] = {
    Right(Result(propertyKey))
  }

  private[this] case class Result(propertyKey: PropertyKey[Key]) extends ValidatedPropertyKey[Key]

}
