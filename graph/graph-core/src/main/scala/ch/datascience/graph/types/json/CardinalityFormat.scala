package ch.datascience.graph.types.json

import ch.datascience.graph.types.Cardinality
import play.api.libs.json._

/**
  * Created by johann on 19/06/17.
  */
object CardinalityFormat extends Format[Cardinality] {

  def writes(cardinality: Cardinality): JsString = JsString(cardinality.name)

  def reads(json: JsValue): JsResult[Cardinality] = json.validate[String] flatMap { str =>
    try {
      JsSuccess(Cardinality(str))
    } catch {
      case e: IllegalArgumentException => JsError(e.getMessage)
    }
  }

}
