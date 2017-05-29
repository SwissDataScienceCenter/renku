package ch.datascience.graph.elements.validation

import ch.datascience.graph.elements.Property
import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.scope.PropertyScope
import ch.datascience.graph.values.BoxedOrValidValue

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 01/05/17.
  */
trait PropertyValidator {

  def validateProperty(
    property: Property
  )(
    implicit e: BoxedOrValidValue[Property#Value],
    ec: ExecutionContext
  ): Future[ValidationResult[ValidatedProperty]] = {
    val future = propertyScope getPropertyFor property.key
    future.map({ definition =>
      this.validatePropertySync(property, definition)(e)
    })(ec)
  }

  def validatePropertySync(
    property: Property,
    definition: Option[PropertyKey]
  )(
    implicit e: BoxedOrValidValue[Property#Value]
  ): ValidationResult[ValidatedProperty] = definition match {
    case None => Left(UnknownProperty(property.key))
    case Some(propertyKey) if property.key != propertyKey.key => Left(WrongDefinition(propertyKey.key, property.key))
    case Some(propertyKey) if property.dataType(e) != propertyKey.dataType => Left(BadDataType(property.key, propertyKey.dataType, property.dataType(e)))
    case Some(propertyKey) => Right(Result(property, propertyKey))
  }

  protected def propertyScope: PropertyScope

  private[this] case class Result(
    property: Property,
    propertyKey: PropertyKey
  ) extends ValidatedProperty

}
