package ch.datascience.graph.naming.json

import ch.datascience.graph.naming.Namespace
import play.api.libs.json._

/**
  * Created by johann on 13/06/17.
  */
object NamespaceReads extends Reads[String] {

  def reads(json: JsValue): JsResult[String] = implicitly[Reads[String]].reads(json).flatMap { str =>
    try {
      JsSuccess(Namespace(str))
    } catch {
      case e: IllegalArgumentException => JsError(e.getMessage)
    }

  }

}
