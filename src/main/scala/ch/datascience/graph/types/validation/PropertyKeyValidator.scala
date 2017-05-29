package ch.datascience.graph.types.validation

import ch.datascience.graph.scope.PropertyScope
import ch.datascience.graph.types.PropertyKey

import scala.concurrent.Future

/**
  * Created by johann on 09/05/17.
  */
trait PropertyKeyValidator {

  def validatePropertyKey(propertyKey: PropertyKey): Future[ValidationResult[ValidatedPropertyKey]] = {
    Future.successful( validatePropertyKeySync(propertyKey) )
  }

  def validatePropertyKeySync(propertyKey: PropertyKey): ValidationResult[ValidatedPropertyKey] = {
    Right(Result(propertyKey))
  }

  private[this] case class Result(propertyKey: PropertyKey) extends ValidatedPropertyKey

}
