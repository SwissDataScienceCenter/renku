package controllers.management

import java.sql.SQLException
import java.util.UUID
import javax.inject.Inject

import ch.datascience.graph.types.Multiplicity
import ch.datascience.graph.types.persistence.model.json._
import controllers.JsonComponent
import injected.OrchestrationLayer
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future

/**
  * Created by johann on 26/04/17.
  */
class EdgeLabelController @Inject()(protected val orchestrator: OrchestrationLayer) extends Controller with JsonComponent {

  def index: Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val all = orchestrator.edgeLabels.all()
    all.map(seq => Json.toJson(seq)).map(json => Ok(json))
  }

  def findById(id: String): Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val json = JsString(id)
    json.validate[UUID] match {
      case JsError(e) => Future.successful(BadRequest(JsError.toJson(e)))
      case JsSuccess(uuid, _) =>
        val future = orchestrator.edgeLabels.findById(uuid)
        future map {
          case Some(edgeLabel) => Ok(Json.toJson(edgeLabel))
          case None => NotFound
        }
    }
  }

  def findByNamespaceAndName(namespace: String, name: String): Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val future = orchestrator.edgeLabels.findByNamespaceAndName(namespace, name)
    future map {
      case Some(edgeLabel) => Ok(Json.toJson(edgeLabel))
      case None => NotFound
    }
  }

  def create: Action[(String, String, Multiplicity)] = Action.async(bodyParseJson[(String, String, Multiplicity)](EdgeLabelRequestFormat)) { implicit request =>
    val (namespace, name, multiplicity) = request.body
    val future = orchestrator.edgeLabels.createEdgeLabel(namespace, name, multiplicity)
    future map { edgeLabel => Ok(Json.toJson(edgeLabel)) } recover {
      case e: SQLException =>
        //TODO: log exception
        Conflict // Avoids send of 500 INTERNAL ERROR if duplicate creation
    }
  }

}
