package ch.datascience.graph.types.json

import ch.datascience.graph.types.Multiplicity
import play.api.libs.json.{JsString, JsValue, Writes}

/**
  * Created by johann on 07/06/17.
  */
object MultiplicityWrites extends Writes[Multiplicity] {

  def writes(multiplicity: Multiplicity): JsString = JsString(multiplicity.name)

}
