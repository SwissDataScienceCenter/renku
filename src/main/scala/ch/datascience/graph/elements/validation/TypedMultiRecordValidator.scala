package ch.datascience.graph.elements.validation

import ch.datascience.graph.elements.{BoxedOrValidValue, Property, MultiRecord, TypedMultiRecord}
import ch.datascience.graph.scope.NamedTypeScope
import ch.datascience.graph.types.{NamedType, PropertyKey, RecordType}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 16/05/17.
  */
trait TypedMultiRecordValidator[TypeId, Key, Value, Prop <: Property[Key, Value, Prop]] { this: MultiRecordValidator[Key, Value, Prop] =>

  def validateTypedMultiRecord(
    record: TypedMultiRecord[TypeId, Key, Value, Prop]
  )(
    implicit e: BoxedOrValidValue[Value],
    ec: ExecutionContext
  ): Future[ValidationResult[ValidatedTypedMultiRecord[TypeId, Key, Value, Prop]]] = {
    for {
      (propertiesDefinitions, namedTypesDefinitions) <- namedTypeScope.getDefinitionsFor(record.types)
    } yield this.validateTypedMultiRecordSync(record, propertiesDefinitions, namedTypesDefinitions)
  }

  def validateTypedMultiRecordSync(
    record: TypedMultiRecord[TypeId, Key, Value, Prop],
    propertyDefinitions: Map[Key, PropertyKey[Key]],
    namedTypeDefinitions: Map[TypeId, NamedType[TypeId, Key]]
  )(
    implicit e: BoxedOrValidValue[Value]
  ): ValidationResult[ValidatedTypedMultiRecord[TypeId, Key, Value, Prop]] = {
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
    record: MultiRecord[Key, Value, Prop],
    recordType: RecordType[Key],
    namedTypeKey: TypeId,
    namedTypeDefinition: Option[NamedType[TypeId, Key]]
  ): Either[ValidationError, NamedType[TypeId, Key]] = namedTypeDefinition match {
    case None => Left(UnknownType(namedTypeKey))
    case Some(namedType) if !(recordType << namedType.like) => Left(MultiRecordTypeError(record, namedType.like, namedType.properties -- recordType.properties))
    case Some(namedType) => Right(namedType)
  }

  protected def namedTypeScope: NamedTypeScope[TypeId, Key]

  private[this] case class Result(
    record: TypedMultiRecord[TypeId, Key, Value, Prop],
    namedTypes: Map[TypeId, NamedType[TypeId, Key]],
    recordType: RecordType[Key],
    propertyKeys: Map[Key, PropertyKey[Key]]
  ) extends ValidatedTypedMultiRecord[TypeId, Key, Value, Prop]

}
