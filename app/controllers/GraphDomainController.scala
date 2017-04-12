package controllers

import javax.inject._

import injected.JanusGraphConfig
import play.api.mvc._

@Singleton
class GraphDomainController @Inject() (protected val janusGraphConfig: JanusGraphConfig) extends Controller {

  def index = Action { implicit request =>
    Ok(janusGraphConfig.get)
  }

}
