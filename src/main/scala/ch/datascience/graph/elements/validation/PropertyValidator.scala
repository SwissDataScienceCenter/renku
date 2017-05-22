package ch.datascience.graph.elements.validation

import ch.datascience.graph.elements.Property
import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.scope.PropertyScope
import ch.datascience.graph.values.BoxedOrValidValue

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 01/05/17.
  */
trait PropertyValidator[Key, Value, Prop <: Property[Key, Value]] {

  def validateProperty(
    property: Prop
  )(
    implicit e: BoxedOrValidValue[Value],
    ec: ExecutionContext
  ): Future[ValidationResult[ValidatedProperty[Key, Value, Prop]]] = {
    val future = propertyScope getPropertyFor property.key
    future.map({ definition =>
      this.validatePropertySync(property, definition)(e)
    })(ec)
  }

  def validatePropertySync(
    property: Prop,
    definition: Option[PropertyKey[Key]]
  )(
    implicit e: BoxedOrValidValue[Value]
  ): ValidationResult[ValidatedProperty[Key, Value, Prop]] = definition match {
    case None => Left(UnknownProperty(property.key))
    case Some(propertyKey) if property.key != propertyKey.key => Left(WrongDefinition(propertyKey.key, property.key))
    case Some(propertyKey) if property.dataType(e) != propertyKey.dataType => Left(BadDataType(property.key, propertyKey.dataType, property.dataType(e)))
    case Some(propertyKey) => Right(Result(property, propertyKey))
  }

  protected def propertyScope: PropertyScope[Key]

  private[this] case class Result(
    property: Prop,
    propertyKey: PropertyKey[Key]
  ) extends ValidatedProperty[Key, Value, Prop]

}
