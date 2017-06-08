package controllers

import javax.inject.{Inject, Singleton}

import ch.datascience.graph.elements.mutation.Mutation
import ch.datascience.graph.elements.mutation.json.MutationFormat
import models.{RequestWorker, ResponseWorker}
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 07/06/17.
  */
@Singleton
class MutationController @Inject()(
  protected val requestWorker: RequestWorker,
  protected val responseWorker: ResponseWorker
) extends Controller
  with JsonComponent {

  implicit lazy val ec: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext

  implicit lazy val mutationFormat: Format[Mutation] = MutationFormat

  def postMutation: Action[Mutation] = Action.async(bodyParseJson[Mutation](MutationFormat)) { implicit request =>
    val query = request.body
    //TODO: get token from header
    val token: JsValue = Json.parse("""{ "my-token-is-empty": true }""")
    val event = JsObject(Seq(
      "query" -> Json.toJson(query),
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
