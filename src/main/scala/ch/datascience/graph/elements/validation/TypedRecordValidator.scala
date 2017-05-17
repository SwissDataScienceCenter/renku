package ch.datascience.graph.elements.validation

import ch.datascience.graph.elements.{BoxedOrValidValue, Property, Record, TypedRecord}
import ch.datascience.graph.scope.NamedTypeScope
import ch.datascience.graph.types.{NamedType, PropertyKey, RecordType}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 16/05/17.
  */
trait TypedRecordValidator[TypeId, Key, Value, Prop <: Property[Key, Value, Prop]] { this: RecordValidator[Key, Value, Prop] =>

  def validateTypedRecord(
    record: TypedRecord[TypeId, Key, Value, Prop]
  )(
    implicit e: BoxedOrValidValue[Value],
    ec: ExecutionContext
  ): Future[ValidationResult[ValidatedTypedRecord[TypeId, Key, Value, Prop]]] = {
    for {
      (propertiesDefinitions, namedTypesDefinitions) <- namedTypeScope.getDefinitionsFor(record.types)
    } yield this.validateTypedRecordSync(record, propertiesDefinitions, namedTypesDefinitions)
  }

  def validateTypedRecordSync(
    record: TypedRecord[TypeId, Key, Value, Prop],
    propertyDefinitions: Map[Key, PropertyKey[Key]],
    namedTypeDefinitions: Map[TypeId, NamedType[TypeId, Key]]
  )(
    implicit e: BoxedOrValidValue[Value]
  ): ValidationResult[ValidatedTypedRecord[TypeId, Key, Value, Prop]] = {
    // Perform record-level validation
    validateRecordSync(record, propertyDefinitions) match {
      case Left(error) => Left(error)
      case Right(validatedRecord) =>
        // Check record conformance to types
        val recordType = validatedRecord.recordType
        val validatedTypes = (for {
          myType <- record.types
          namedTypeDefinition = namedTypeDefinitions get myType
        } yield myType -> validateOneRecordTypeSync(record, recordType, myType, namedTypeDefinition)).toMap

        val errors = validatedTypes.values.flatMap(_.left.toOption)

        if (errors.isEmpty) {
          val validNamedTypes = for {
            (key, validated) <- validatedTypes
            v <- validated.right.toOption
          } yield key -> v
          Right(Result(record, validNamedTypes, recordType, validatedRecord.propertyKeys))
        }
        else
          Left(MultipleErrors.make(errors.toSeq))
    }
  }

  protected def validateOneRecordTypeSync(
    record: Record[Key, Value, Prop],
    recordType: RecordType[Key],
    namedTypeKey: TypeId,
    namedTypeDefinition: Option[NamedType[TypeId, Key]]
  ): Either[ValidationError, NamedType[TypeId, Key]] = namedTypeDefinition match {
    case None => Left(UnknownType(namedTypeKey))
    case Some(namedType) if !(recordType << namedType.like) => Left(RecordTypeError(record, namedType.like, namedType.properties -- recordType.properties))
    case Some(namedType) => Right(namedType)
  }

  protected def namedTypeScope: NamedTypeScope[TypeId, Key]

  private[this] case class Result(
    record: TypedRecord[TypeId, Key, Value, Prop],
    namedTypes: Map[TypeId, NamedType[TypeId, Key]],
    recordType: RecordType[Key],
    propertyKeys: Map[Key, PropertyKey[Key]]
  ) extends ValidatedTypedRecord[TypeId, Key, Value, Prop]

}
