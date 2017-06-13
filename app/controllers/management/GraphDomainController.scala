package controllers.management

import java.sql.SQLException
import java.util.UUID
import javax.inject.Inject

import ch.datascience.graph.types.persistence.model.json._
import controllers.JsonComponent
import injected.OrchestrationLayer
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._

class GraphDomainController @Inject()(protected val orchestrator: OrchestrationLayer) extends Controller with JsonComponent {

  def index: Action[AnyContent] = Action.async { implicit request =>
    val all = orchestrator.graphDomains.all()
    all.map(seq => Json.toJson(seq)).map(json => Ok(json))
  }

  def findByIdOrName(idOrName: String): Action[Unit] = Action.async(BodyParsers.parse.empty) { implicit request =>
    val json = JsString(idOrName)
    val future = json.validate[UUID].asOpt match {
      case Some(id) => orchestrator.graphDomains.findById(id)
      case None => orchestrator.graphDomains.findByNamespace(idOrName)
    }
    future map {
      case Some(graphDomain) => Ok(Json.toJson(graphDomain))
      case None => NotFound
    }
  }

  def create: Action[String] = Action.async(bodyParseJson[String](GraphDomainRequestFormat)) { implicit request =>
    val namespace = request.body
    val future = orchestrator.graphDomains.createGraphDomain(namespace)
    future map { graphDomain => Ok(Json.toJson(graphDomain)) } recover {
      case e: SQLException =>
        //TODO: log exception
        Conflict // Avoids send of 500 INTERNAL ERROR if duplicate creation
    }
  }

}
