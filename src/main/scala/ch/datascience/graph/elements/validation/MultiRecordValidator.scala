package ch.datascience.graph.elements.validation

import ch.datascience.graph.Constants.Key
import ch.datascience.graph.elements.{MultiRecord, Property}
import ch.datascience.graph.types.{PropertyKey, RecordType}
import ch.datascience.graph.values.BoxedOrValidValue

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 08/05/17.
  */
trait MultiRecordValidator { this: MultiPropertyValidator =>

  def validateMultiRecord(
    record: MultiRecord
  )(
    implicit e: BoxedOrValidValue[MultiRecord#Prop#Value],
    ec: ExecutionContext
  ): Future[ValidationResult[ValidatedMultiRecord]] = {
    val future = propertyScope.getPropertiesFor(record.properties.keySet)
    future.map({ definitions =>
      this.validateMultiRecordSync(record, definitions)
    })(ec)
  }

  def validateMultiRecordSync(
    record: MultiRecord,
    definitions: Map[PropertyKey#Key, PropertyKey]
  )(
    implicit e: BoxedOrValidValue[MultiRecord#Prop#Value]
  ): ValidationResult[ValidatedMultiRecord] = {
    // First validate that the key -> property mapping is consistent
    val consistencyErrors = for {
      (key, property) <- record.properties
      if key != property.key
    } yield BadRecord(key, property.key)

    // Use PropertyValidator method validatePropertySync to validate each property
    val validatedProperties = for {
      (key, property) <- record.properties
      definition = definitions.get(key)
    } yield key -> this.validateMultiPropertySync(property, definition)
    val invalidPropertyErrors = validatedProperties.values.flatMap(_.left.toOption)

    // Collect errors
    val allErrors = consistencyErrors ++ invalidPropertyErrors

    if (allErrors.isEmpty) {
      val validProperties = for {
        (key, validated) <- validatedProperties
        v <- validated.right.toOption
      } yield key -> v.propertyKey
      Right(Result(record, RecordType(record.properties.keySet), validProperties))
    }
    else
      Left(MultipleErrors.make(allErrors.toSeq))
  }

  private[this] case class Result(
    record: MultiRecord,
    recordType: RecordType,
    propertyKeys: Map[Key, PropertyKey]
  ) extends ValidatedMultiRecord

}
