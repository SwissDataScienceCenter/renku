package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.Property
import play.api.libs.json.{JsValue, Writes}

/**
  * Created by johann on 24/05/17.
  */
class LeafPropertyWrites[Key, Value : Writes, Prop <: Property[Key, Value]] extends Writes[Prop] {

  // Just write the value
  def writes(property: Prop): JsValue = implicitly[Writes[Value]].writes(property.value)

}
