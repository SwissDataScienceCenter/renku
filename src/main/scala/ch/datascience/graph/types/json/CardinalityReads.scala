package ch.datascience.graph.types.json

import ch.datascience.graph.types.Cardinality
import play.api.libs.json._

/**
  * Created by johann on 17/05/17.
  */
object CardinalityReads extends Reads[Cardinality] {

  def reads(json: JsValue): JsResult[Cardinality] = json.validate[String] flatMap { str =>
    try {
      JsSuccess(Cardinality(str))
    } catch {
      case e: IllegalArgumentException => JsError(e.getMessage)
    }
  }

}
