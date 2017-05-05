package ch.datascience.graph.typecheck

import ch.datascience.graph.elements.{BoxedOrValidValue, Property}
import ch.datascience.graph.types.PropertyKey

/**
  * Created by johann on 01/05/17.
  */
trait PropertyChecker[Key, Value, Prop <: Property[Key, Value, Prop]] {

  def checkProperty(
    property: Property[Key, Value, Prop],
    definitions: Map[Key, PropertyKey[Key]]
  )(
    implicit e: BoxedOrValidValue[Value]
  ): ValidationResult[PropertyChecked[Key]] = {
    definitions get property.key match {
      case Some(pk) if property.dataType(e) == pk.dataType => Right(Result(pk))
      case Some(pk)                                        => Left(BadDataType(property.value, pk.dataType, property.dataType(e)))
      case None                                            => Left(UnknownProperty(property.key))
    }
  }

  private[this] case class Result(propertyKey: PropertyKey[Key]) extends PropertyChecked[Key]

}
