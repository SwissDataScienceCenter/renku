package ch.datascience.graph.types.json

import ch.datascience.graph.types.Multiplicity
import play.api.libs.json._

/**
  * Created by johann on 07/06/17.
  */
object MultiplicityReads extends Reads[Multiplicity] {

  def reads(json: JsValue): JsResult[Multiplicity] = json.validate[String] flatMap { str =>
    try {
      JsSuccess(Multiplicity(str))
    } catch {
      case e: IllegalArgumentException => JsError(e.getMessage)
    }
  }

}
