package ch.datascience.typesystem.orchestration

import java.util.UUID

import ch.datascience.typesystem.scope.StandardScope

import scala.concurrent.Future

/**
  * Created by johann on 24/04/17.
  */
trait ScopeComponent { this: DatabaseComponent with ExecutionComponent =>

  import profile.api._

  type Scope = StandardScope[String, String]

  private[this] var scope: Scope = StandardScope.empty[String, String]

  def getCurrentScopeSync: Scope = synchronized {
    scope
  }

  def getCurrentScope: Future[Scope] = Future { getCurrentScopeSync }

  def scopeForPropertyKey(namespace: String, name: String): Future[Scope] = {
    val candidateScope = getCurrentScope
    val key = s"$namespace:$name"
    candidateScope flatMap { scope =>
      scope.propertyDefinitions contains key match {
        case true => Future.successful(scope)
        case false =>
          val select = dal.propertyKeys.findByNamespaceAndNameAsModel(namespace, name).result.headOption
          db.run(select) map {
            case Some(propertyKey) =>
              val newScope = scope + propertyKey
              synchronized {
                this.scope = newScope
              }
              newScope
            case None => scope
          }
      }
    }
  }

}
