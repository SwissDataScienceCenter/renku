package ch.datascience.graph.typevalidation

import ch.datascience.graph.elements.{BoxedOrValidValue, Property, Record}
import ch.datascience.graph.types.PropertyKey

import scala.collection.generic.CanBuildFrom
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 08/05/17.
  */
trait RecordValidator[Key, Value, Prop <: Property[Key, Value, Prop]] { this: PropertyValidator[Key, Value, Prop] =>

  def validateRecord(
    record: Record[Key, Value, Prop]
  )(
    implicit e: BoxedOrValidValue[Value],
    ec: ExecutionContext
  ): Future[ValidationResult[ValidatedRecord[Key]]] = {
    val future = propertyScope.getPropertiesFor(record.properties.keySet)
    future.map({ definitions =>
      this.validateRecordSync(record, definitions)
    })(ec)
  }

  def validateRecordSync(
    record: Record[Key, Value, Prop],
    definitions: Map[Key, PropertyKey[Key]]
  )(
    implicit e: BoxedOrValidValue[Value]
  ): ValidationResult[ValidatedRecord[Key]] = {
    // We collect all errors
    val errors = Seq.newBuilder[ValidationError]

    // First validate that the key -> property mapping is consistent
    for ((key, property) <- record.properties) {
      if (key != property.key)
        errors += BadRecord(key, property.key)
    }

    val validatedProperties = for {
      (key, property) <- record.properties
      definition = definitions.get(key)
    } yield key -> this.validatePropertySync(property, definition)(e)

    val invalidProperties = errors.result() ++ validatedProperties.values.flatMap(_.left.toOption)

    if (invalidProperties.isEmpty) {
      val validProperties = for {
        (key, validated) <- validatedProperties
        v <- validated.right.toOption
      } yield key -> v.propertyKey
      Right(Result(validProperties))
    }
    else
      Left(MultipleErrors.make(invalidProperties.toSeq))
  }

  private[this] case class Result(propertyKeys: Map[Key, PropertyKey[Key]]) extends ValidatedRecord[Key]

}
