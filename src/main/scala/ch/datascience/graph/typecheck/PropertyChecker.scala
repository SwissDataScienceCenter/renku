package ch.datascience.graph.typecheck

import ch.datascience.graph.elements.{BoxedOrValidValue, HasValueMapper, Property}
import ch.datascience.graph.types.PropertyKey

import scala.util.{Failure, Success, Try}

/**
  * Created by johann on 01/05/17.
  */
trait PropertyChecker[Key, Value, Prop <: Property[Key, Value, Prop]] {

  def checkProperty(prop: Property[Key, Value, Prop], definitions: Map[Key, PropertyKey[Key]])(implicit e: BoxedOrValidValue[Value]): ValidationResult[PropertyChecked[Key]] = {
    definitions get prop.key match {
      case Some(pk) =>
        if (prop.dataType(e) == pk.dataType)
          Right(Result(pk))
        else
          Left(BadDataType(prop.value, pk.dataType, prop.dataType(e)))
      case None => Left(UnknownProperty(prop.key))
    }
  }

  private[this] case class Result(propertyKey: PropertyKey[Key]) extends PropertyChecked[Key]

}
