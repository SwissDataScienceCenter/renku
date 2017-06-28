package ch.datascience.graph.elements.detached.json

import ch.datascience.graph.elements.RichProperty
import ch.datascience.graph.elements.detached.{DetachedProperty, DetachedRichProperty}
import ch.datascience.graph.elements.json.{RichPropertyReads, RichPropertyWrites}
import play.api.libs.json._

/**
  * Created by johann on 31/05/17.
  */
object DetachedRichPropertyFormat extends Format[DetachedRichProperty] {

  def writes(prop: DetachedRichProperty): JsValue = writer.writes(prop)

  def reads(json: JsValue): JsResult[DetachedRichProperty] = for {
    prop <- reader.reads(json)
  } yield DetachedRichProperty(prop.key, prop.value, prop.properties)

  private[this] lazy val writer: Writes[RichProperty { type Prop = DetachedProperty }] = new RichPropertyWrites[DetachedProperty]()(DetachedPropertyFormat)

  private[this] lazy val reader: Reads[RichProperty { type Prop = DetachedProperty }] = new RichPropertyReads[DetachedProperty]()(DetachedPropertyFormat)

}
