package controllers

import javax.inject._

import injected.OrchestrationLayer
import play.api.mvc._

@Singleton
class GraphDomainController @Inject()(protected val orchestrator: OrchestrationLayer) extends Controller {

  def index = Action { implicit request =>
    println(orchestrator.graphDomains.all())
    Ok
  }

}
