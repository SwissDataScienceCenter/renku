package controllers

import javax.inject._
import injected.OrchestrationLayer
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class DeployController @Inject()(protected val orchestrator: OrchestrationLayer,
                               config: play.api.Configuration) extends Controller {

  def deploy = Action.async { implicit request =>
    Future {
      Ok()
    }
  }

  def register(id: Long) = Action.async { implicit request =>
    Future {
      Ok()
    }
  }

}
