package ch.datascience.graph.elements.validation

import ch.datascience.graph.elements.{MultiPropertyValue, Property}
import ch.datascience.graph.scope.PropertyScope
import ch.datascience.graph.types.PropertyKey

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 01/05/17.
  */
trait MultiPropertyValidator {

  def validateMultiProperty(
    property: MultiPropertyValue[Property]
  )(
    implicit ec: ExecutionContext
  ): Future[ValidationResult[ValidatedMultiProperty]] = {
    val future = propertyScope getPropertyFor property.key
    future.map({ definition =>
      this.validateMultiPropertySync(property, definition)
    })(ec)
  }

  def validateMultiPropertySync(
    property: MultiPropertyValue[Property],
    definition: Option[PropertyKey]
  ): ValidationResult[ValidatedMultiProperty] = definition match {
    case None => Left(UnknownProperty(property.key))
    case Some(propertyKey) if property.key != propertyKey.key => Left(WrongDefinition(propertyKey.key, property.key))
    case Some(propertyKey) if property.dataType != propertyKey.dataType => Left(BadDataType(property.key, propertyKey.dataType, property.dataType))
    case Some(propertyKey) if property.cardinality != propertyKey.cardinality => Left(BadCardinality(property.key, propertyKey.cardinality, property.cardinality))
    case Some(propertyKey) => Right(Result(property, propertyKey))
  }

  protected def propertyScope: PropertyScope

  private[this] case class Result(
    properties: MultiPropertyValue[Property],
    propertyKey: PropertyKey
  ) extends ValidatedMultiProperty

}
