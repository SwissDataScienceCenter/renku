package controllers

import javax.inject.Inject

import injected.OrchestrationLayer
import play.api.libs.json._
import play.api.mvc._

/**
  * Created by johann on 26/04/17.
  */
class ValidationController @Inject()(protected val orchestrator: OrchestrationLayer) extends Controller with JsonComponent {

  def validateObject: Action[JsValue] = Action.async(BodyParsers.parse.json) { implicit request =>
    val obj = request.body
    ???
  }

}
