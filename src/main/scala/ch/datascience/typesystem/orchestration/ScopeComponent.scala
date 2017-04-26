package ch.datascience.typesystem
package orchestration

import ch.datascience.typesystem.model.{PropertyKey, RecordType}
import ch.datascience.typesystem.model.base.{GraphObjectBase, NamedRecordTypeBase, PropertyKeyBase, RecordTypeBase}
import ch.datascience.typesystem.scope.ConcurrentScope
import ch.datascience.typesystem.validation.Validator

import scala.concurrent.{Future, blocking}
/**
  * Created by johann on 26/04/17.
  */
trait ScopeComponent { this: PropertyKeyComponent with ExecutionComponent =>

  type Typ = String
  type Prop = String
  type NamedRecordType = NamedRecordTypeBase[Typ, Prop]
  type GraphObject = GraphObjectBase[Typ, Prop]

  type ConcurrentScopeType = ConcurrentScope[Typ, Prop, PropertyKey, RecordType, NamedRecordType, GraphObject]
  type ValidatorType = Validator[Typ, Prop, PropertyKey, RecordType, NamedRecordType, GraphObject]

  protected def scope: ConcurrentScopeType

  def getCurrentValidator: Future[ValidatorType] = Future {
    blocking { scope.getCurrentValidator }
  }

  def getValidatorForPropertyKey(namespace: String, name: String): Future[ValidatorType] = {
    this.getCurrentValidator flatMap { validator =>
      val key = s"$namespace:$name"
      validator.propertyKeys contains key match {
        case true => Future.successful(validator)
        case false =>
          val optPropertyKey = this.propertyKeys.findByNamespaceAndName(namespace, name)
          optPropertyKey map {
            case Some(propertyKey) =>
              this.scope += propertyKey
              this.scope.getCurrentValidator
            case None => validator
          }
      }
    }
  }

}
