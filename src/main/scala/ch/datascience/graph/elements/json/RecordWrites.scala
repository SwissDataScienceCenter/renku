package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.Record
import play.api.libs.json.{JsPath, JsValue, Writes}

/**
  * Created by johann on 24/05/17.
  */
class RecordWrites(implicit sw: StringWrites[Record#Prop#Key], vw: Writes[Record#Prop]) extends Writes[Record] {

  def writes(record: Record): JsValue = (JsPath \ "properties").write[Record#Properties].writes(record.properties)

  private[this] implicit lazy val mapWrites: Writes[Record#Properties] = sw.mapWrites[Record#Prop](vw)

}
