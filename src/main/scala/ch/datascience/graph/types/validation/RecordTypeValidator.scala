package ch.datascience.graph.types.validation

import ch.datascience.graph.scope.PropertyScope
import ch.datascience.graph.types.{PropertyKey, RecordType}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 10/05/17.
  */
trait RecordTypeValidator {

  def validateRecord(
    recordType: RecordType
  )(
    implicit ec: ExecutionContext
  ): Future[ValidationResult[ValidatedRecordType]] = {
    val future = propertyScope.getPropertiesFor(recordType.properties)
    future.map({ definitions =>
      this.validateRecordTypeSync(recordType, definitions)
    })(ec)
  }

  def validateRecordTypeSync(
    recordType: RecordType,
    definitions: Map[PropertyKey#Key, PropertyKey]
  ): ValidationResult[ValidatedRecordType] = {
    // Check that properties have definitions
    val errors = for {
      key <- recordType.properties
      if !(definitions contains key)
    } yield UnknownProperty(key)

    if (errors.isEmpty) {
      val propertyKeys = definitions filterKeys { recordType.properties contains _ }
      Right(Result(recordType, propertyKeys))
    }
    else
      Left(MultipleErrors.make(errors.toSeq))
  }

  protected def propertyScope: PropertyScope

  private[this] case class Result(recordType: RecordType, propertyKeys: Map[PropertyKey#Key, PropertyKey])
    extends ValidatedRecordType

}
