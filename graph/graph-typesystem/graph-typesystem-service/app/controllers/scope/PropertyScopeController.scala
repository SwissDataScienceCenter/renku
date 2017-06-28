package controllers.scope

import javax.inject.Inject

import ch.datascience.graph.Constants
import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.scope.PropertyScope
import ch.datascience.graph.scope.persistence.json._
import ch.datascience.graph.types.json._
import controllers.JsonComponent
import injected.{OrchestrationLayer, ScopeBean}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future
import scala.util.Try

/**
  * Created by johann on 11/05/17.
  */
class PropertyScopeController @Inject()(protected val scopeLayer: ScopeBean, protected val orchestrator: OrchestrationLayer) extends Controller with JsonComponent {

  protected val scope: PropertyScope = scopeLayer

//  implicit val ec: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext

//  def getCachedProperties: Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
//    scope.getCachedProperties map { properties =>
//      val json = JsObject(
//        for {
//          (key, property) <- properties
//        } yield key.asString -> Json.toJson(property)(PropertyKeyFormat)
//      )
//      Ok(json)
//    }
//  }

  def index: Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val all = orchestrator.propertyKeys.all()
    for {
      seq <- all
    } yield {
      val propertyKeys = for {
        pk <- seq
      } yield pk.toStandardPropertyKey
      Ok(Json.toJson(propertyKeys)(Writes.seq(PropertyKeyFormat)))
    }
  }

  def getPropertyFor(namespace: String, name: String): Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val futureKey = Future.fromTry(Try( NamespaceAndName(namespace, name) ))
    val futureProperty = futureKey flatMap { key => scope.getPropertyFor(key) }
    futureProperty map {
      case Some(property) => Ok(Json.toJson(property)(PropertyKeyFormat))
      case None => NotFound
    } recover {
      case e: IllegalArgumentException => BadRequest(e.getMessage)
    }
  }

  def getPropertiesFor: Action[Set[Constants.Key]] = Action.async(bodyParseJson[Set[Constants.Key]]) { implicit request =>
    val keys = request.body
    for {
      propertyKeys <- scope.getPropertiesFor(keys)
    } yield Ok(Json.toJson(propertyKeys))
  }


//  def getPropertiesFor(keys: Set[NamespaceAndName]): Future[Map[NamespaceAndName, PropertyKey[NamespaceAndName]]] = scope.getPropertiesFor(keys)

//  implicit lazy val propertyKeyWrites: Writes[PropertyKey[NamespaceAndName]] = (
//    (JsPath \ "key").write[String] and
//      (JsPath \ "cardinality").write[Cardinality](CardinalityMappers.cardinalityWrites) and
//      (JsPath \ "dataType").write[DataType](DataTypeMappers.dataTypeWrites)
//    )({ p: PropertyKey[NamespaceAndName] => (p.key.asString, p.cardinality, p.dataType) })

}
