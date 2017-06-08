package controllers

import javax.inject.{Inject, Singleton}

import models.{RequestWorker, ResponseWorker}
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 07/06/17.
  */
@Singleton
class MutationController @Inject()(
  protected val requestWorker: RequestWorker,
  protected val responseWorker: ResponseWorker
) extends Controller {

  implicit lazy val ec: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext

  def postMutation: Action[JsValue] = Action.async(BodyParsers.parse.json) { implicit request =>
    val query = request.body
    //TODO: get token from header
    val token: JsValue = Json.parse("""{ "my-token-is-empty": true }""")
    val event = JsObject(Seq(
      "query" -> query,
      "token" -> token
    ))

    for {
      event <- requestWorker.add(event)
    } yield {
      val json = JsObject(Seq(
        "uuid" -> JsString(event.uuid.toString),
        "event" -> event.event,
        "timestamp" -> JsString(event.created.atZone(java.time.ZoneId.systemDefault).toString)
      ))
      Ok(json)
    }
  }

}
