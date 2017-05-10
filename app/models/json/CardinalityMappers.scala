package models.json

import ch.datascience.graph.types.Cardinality
import play.api.libs.json._

/**
  * Created by johann on 26/04/17.
  */
object CardinalityMappers {

  def cardinalityWrites: Writes[Cardinality] = new Writes[Cardinality] {
    def writes(cardinality: Cardinality): JsString = JsString(cardinality.name)
  }

  def cardinalityReads: Reads[Cardinality] = new Reads[Cardinality] {
    def reads(json: JsValue): JsResult[Cardinality] = json.validate[String] flatMap { str =>
      try {
        JsSuccess(Cardinality(str))
      } catch {
        case e: IllegalArgumentException => JsError(e.getMessage)
      }
    }
  }

}
