package ch.datascience.graph.types.json

import ch.datascience.graph.types.Multiplicity
import play.api.libs.json._

/**
  * Created by johann on 19/06/17.
  */
object MultiplicityFormat extends Format[Multiplicity] {

  def writes(multiplicity: Multiplicity): JsString = JsString(multiplicity.name)
  
  def reads(json: JsValue): JsResult[Multiplicity] = json.validate[String] flatMap { str =>
    try {
      JsSuccess(Multiplicity(str))
    } catch {
      case e: IllegalArgumentException => JsError(e.getMessage)
    }
  }

}
