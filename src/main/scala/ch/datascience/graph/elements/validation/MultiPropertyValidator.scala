package ch.datascience.graph.elements.validation

import ch.datascience.graph.elements.{MultiPropertyValue, Property}
import ch.datascience.graph.scope.PropertyScope
import ch.datascience.graph.types.PropertyKey

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 01/05/17.
  */
trait MultiPropertyValidator[Key, Value, Prop <: Property[Key, Value]] {

  def validateMultiProperty(
    property: MultiPropertyValue[Key, Value, Prop]
  )(
    implicit ec: ExecutionContext
  ): Future[ValidationResult[ValidatedMultiProperty[Key, Value, Prop]]] = {
    val future = propertyScope getPropertyFor property.key
    future.map({ definition =>
      this.validateMultiPropertySync(property, definition)
    })(ec)
  }

  def validateMultiPropertySync(
    property: MultiPropertyValue[Key, Value, Prop],
    definition: Option[PropertyKey[Key]]
  ): ValidationResult[ValidatedMultiProperty[Key, Value, Prop]] = definition match {
    case None => Left(UnknownProperty(property.key))
    case Some(propertyKey) if property.key != propertyKey.key => Left(WrongDefinition(propertyKey.key, property.key))
    case Some(propertyKey) if property.dataType != propertyKey.dataType => Left(BadDataType(property.key, propertyKey.dataType, property.dataType))
    case Some(propertyKey) if property.cardinality != propertyKey.cardinality => Left(BadCardinality(propertyKey.cardinality, property.cardinality))
    case Some(propertyKey) => Right(Result(property, propertyKey))
  }

  protected def propertyScope: PropertyScope[Key]

  private[this] case class Result(
    properties: MultiPropertyValue[Key, Value, Prop],
    propertyKey: PropertyKey[Key]
  ) extends ValidatedMultiProperty[Key, Value, Prop]

}
