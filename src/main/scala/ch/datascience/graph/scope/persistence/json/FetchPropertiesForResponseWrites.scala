package ch.datascience.graph.scope.persistence.json

import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.types.json.PropertyKeyFormat
import play.api.libs.json.{JsValue, Writes}

/**
  * Created by johann on 23/05/17.
  */
class FetchPropertiesForResponseWrites(implicit w: Writes[PropertyKey#Key]) extends Writes[Map[PropertyKey#Key, PropertyKey]] {

  def writes(definitions: Map[PropertyKey#Key, PropertyKey]): JsValue = seqWrites.writes(definitions.values)

  private[this] lazy val seqWrites = Writes.traversableWrites(PropertyKeyFormat)

//  private[this] implicit lazy val propertyKeyWrites: Writes[PropertyKey] = new PropertyKeyWrites

}
