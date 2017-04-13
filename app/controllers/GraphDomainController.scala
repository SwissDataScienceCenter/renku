package controllers

import javax.inject._

import injected.OrchestrationLayer
import models.json.GraphDomainMappers._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc._

@Singleton
class GraphDomainController @Inject()(protected val orchestrator: OrchestrationLayer) extends Controller {

  def index: Action[AnyContent] = Action.async { implicit request =>
    val all = orchestrator.graphDomains.all()
    all.map(seq => Json.toJson(seq)).map(json => Ok(json))
  }

}
