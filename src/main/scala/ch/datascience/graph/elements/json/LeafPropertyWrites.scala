package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.Property
import play.api.libs.json.{JsValue, Writes}

/**
  * Created by johann on 24/05/17.
  */
class LeafPropertyWrites(implicit w: Writes[Property#Value]) extends Writes[Property] {

  // Just write the value
  def writes(property: Property): JsValue = w.writes(property.value)

}
