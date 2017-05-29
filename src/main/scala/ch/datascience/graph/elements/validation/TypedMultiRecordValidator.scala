package ch.datascience.graph.elements.validation

import ch.datascience.graph.Constants.Key
import ch.datascience.graph.elements.{MultiRecord, Property, TypedMultiRecord}
import ch.datascience.graph.scope.NamedTypeScope
import ch.datascience.graph.types.{NamedType, PropertyKey, RecordType}
import ch.datascience.graph.values.BoxedOrValidValue

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 16/05/17.
  */
trait TypedMultiRecordValidator { this: MultiRecordValidator =>

  def validateTypedMultiRecord(
    record: TypedMultiRecord
  )(
    implicit e: BoxedOrValidValue[TypedMultiRecord#Prop#Value],
    ec: ExecutionContext
  ): Future[ValidationResult[ValidatedTypedMultiRecord]] = {
    for {
      (propertiesDefinitions, namedTypesDefinitions) <- namedTypeScope.getDefinitionsFor(record.types)
    } yield this.validateTypedMultiRecordSync(record, propertiesDefinitions, namedTypesDefinitions)
  }

  def validateTypedMultiRecordSync(
    record: TypedMultiRecord,
    propertyDefinitions: Map[PropertyKey#Key, PropertyKey],
    namedTypeDefinitions: Map[NamedType#TypeId, NamedType]
  )(
    implicit e: BoxedOrValidValue[TypedMultiRecord#Prop#Value]
  ): ValidationResult[ValidatedTypedMultiRecord] = {
    // Perform record-level validation
    validateMultiRecordSync(record, propertyDefinitions) match {
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
    record: MultiRecord,
    recordType: RecordType,
    namedTypeId: NamedType#TypeId,
    namedTypeDefinition: Option[NamedType]
  ): Either[ValidationError, NamedType] = namedTypeDefinition match {
    case None => Left(UnknownType(namedTypeId))
    case Some(namedType) if !(recordType << namedType.like) => Left(MultiRecordTypeError(record, namedType.like, namedType.properties -- recordType.properties))
    case Some(namedType) => Right(namedType)
  }

  protected def namedTypeScope: NamedTypeScope

  private[this] case class Result(
    record: TypedMultiRecord,
    namedTypes: Map[NamedType#TypeId, NamedType],
    recordType: RecordType,
    propertyKeys: Map[Key, PropertyKey]
  ) extends ValidatedTypedMultiRecord

}
