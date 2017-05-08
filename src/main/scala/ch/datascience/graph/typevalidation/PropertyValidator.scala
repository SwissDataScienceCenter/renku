package ch.datascience.graph.typevalidation

import ch.datascience.graph.elements.{BoxedOrValidValue, Property}
import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.typevalidation.scope.PropertyScope

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 01/05/17.
  */
trait PropertyValidator[Key, Value, Prop <: Property[Key, Value, Prop]] {

  def validateProperty(
    property: Property[Key, Value, Prop]
  )(
    implicit e: BoxedOrValidValue[Value],
    ec: ExecutionContext
  ): Future[ValidationResult[ValidatedProperty[Key]]] = {
    val future = propertyScope getPropertyFor property.key
    future.map({ definition =>
      this.validatePropertySync(property, definition)(e)
    })(ec)
  }

  def validatePropertySync(
    property: Property[Key, Value, Prop],
    definition: Option[PropertyKey[Key]]
  )(
    implicit e: BoxedOrValidValue[Value]
  ): ValidationResult[ValidatedProperty[Key]] = definition match {
    case Some(propertyKey) if property.dataType(e) == propertyKey.dataType => Right(Result(propertyKey))
    case Some(propertyKey) => Left(BadDataType(property.value, propertyKey.dataType, property.dataType(e)))
    case None => Left(UnknownProperty(property.key))
  }

  protected def propertyScope: PropertyScope[Key]

  private[this] case class Result(propertyKey: PropertyKey[Key]) extends ValidatedProperty[Key]

}
