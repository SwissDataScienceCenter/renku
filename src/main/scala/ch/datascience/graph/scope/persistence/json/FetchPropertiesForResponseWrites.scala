package ch.datascience.graph.scope.persistence.json

import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.types.json.PropertyKeyWrites
import play.api.libs.json.{JsString, JsValue, Writes}

/**
  * Created by johann on 23/05/17.
  */
class FetchPropertiesForResponseWrites[Key : Writes] extends Writes[Map[Key, PropertyKey[Key]]] {

  def writes(definitions: Map[Key, PropertyKey[Key]]): JsValue = seqWrites.writes(definitions.values)

  private[this] lazy val seqWrites = implicitly[Writes[Iterable[PropertyKey[Key]]]]

  private[this] implicit lazy val propertyKeyWrites: Writes[PropertyKey[Key]] = new PropertyKeyWrites[Key]

}
