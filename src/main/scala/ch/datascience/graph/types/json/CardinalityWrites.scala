package ch.datascience.graph.types.json

import ch.datascience.graph.types.Cardinality
import play.api.libs.json.{JsString, Writes}

/**
  * Created by johann on 17/05/17.
  */
object CardinalityWrites extends Writes[Cardinality] {

  def writes(cardinality: Cardinality): JsString = JsString(cardinality.name)

}
