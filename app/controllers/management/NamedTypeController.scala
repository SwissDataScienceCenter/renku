package controllers.management

import java.sql.SQLException
import java.util.UUID
import javax.inject.Inject

import ch.datascience.graph.naming.NamespaceAndName
import controllers.JsonComponent
import injected.OrchestrationLayer
//import models.json._
import ch.datascience.graph.types.persistence.model.json.{NamedTypeFormat, NamedTypeRequestFormat}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future

/**
  * Created by johann on 15/05/17.
  */
class NamedTypeController @Inject()(protected val orchestrator: OrchestrationLayer) extends Controller with JsonComponent {

  def index: Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val all = orchestrator.namedTypes.all()
    all.map(seq => Json.toJson(seq)).map(json => Ok(json))
  }

  def findById(id: String): Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val json = JsString(id)
    json.validate[UUID] match {
      case JsError(e) => Future.successful(BadRequest(JsError.toJson(e)))
      case JsSuccess(uuid, _) =>
        val future = orchestrator.namedTypes.findById(uuid)
        future  map {
          case Some(namedType) => Ok(Json.toJson(namedType))
          case None => NotFound
        }
    }
  }

  def findByNamespaceAndName(namespace: String, name: String): Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val future = orchestrator.namedTypes.findByNamespaceAndName(namespace, name)
    future map {
      case Some(namedTypes) => Ok(Json.toJson(namedTypes))
      case None => NotFound
    }
  }

  def create: Action[(String, String, Seq[NamespaceAndName], Seq[NamespaceAndName])] = Action.async(bodyParseJson[(String, String, Seq[NamespaceAndName], Seq[NamespaceAndName])](NamedTypeRequestFormat)) { implicit request =>
    val (namespace, name, superTypes, properties) = request.body
    val future = orchestrator.namedTypes.createNamedType(namespace, name, superTypes.toSet, properties.toSet)
    future map { namedType => Ok(Json.toJson(namedType)) } recover {
      case e: SQLException =>
        //TODO: log exception
        Conflict // Avoids send of 500 INTERNAL ERROR if duplicate creation
    }
  }

//  case class CreateNamedType (namespace: String,
//                                           name: String,
//                                           superTypes: Set[NamespaceAndName],
//                                           properties: Set[NamespaceAndName])

//  private[this] lazy val createReads: Reads[CreateNamedType] = (
//    (JsPath \ "namespace").read[String](namespaceReads) and
//      (JsPath \ "name").read[String](nameReads) and
//      (JsPath \ "super_types").read[Seq[NamespaceAndName]] and
//      (JsPath \ "properties").read[Seq[NamespaceAndName]]
//    )({ (namespace, name, superTypes, properties) => CreateNamedType(namespace, name, superTypes.toSet, properties.toSet) })

}
