package ch.datascience.graph.elements.new_.json

import ch.datascience.graph.elements.RichProperty
import ch.datascience.graph.elements.detached.DetachedProperty
import ch.datascience.graph.elements.detached.json.DetachedPropertyFormat
import ch.datascience.graph.elements.json.{RichPropertyReads, RichPropertyWrites}
import ch.datascience.graph.elements.new_.NewRichProperty
import ch.datascience.graph.elements.persisted.Path
import ch.datascience.graph.elements.persisted.json.PathMappers
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 19/06/17.
  */
object NewRichPropertyFormat extends Format[NewRichProperty] {

  def writes(prop: NewRichProperty): JsValue = writer.writes(prop)

  def reads(json: JsValue): JsResult[NewRichProperty] = reader.reads(json)

  private[this] lazy val writer: Writes[NewRichProperty] = (
    (JsPath \ "parent").write[Path](PathMappers.PathFormat) and
      JsPath.write[RichProperty { type Prop = DetachedProperty }](propertyWriter)
    ) { prop => (prop.parent, prop) }

  private[this] lazy val reader: Reads[NewRichProperty] = (
    (JsPath \ "parent").read[Path](PathMappers.PathFormat) and
      JsPath.read[RichProperty { type Prop = DetachedProperty }](propertyReader)
    ) { (parent, prop) => NewRichProperty(parent, prop.key, prop.value, prop.properties) }

  private[this] lazy val propertyWriter: Writes[RichProperty { type Prop = DetachedProperty }] = new RichPropertyWrites[DetachedProperty]()(DetachedPropertyFormat)

  private[this] lazy val propertyReader: Reads[RichProperty { type Prop = DetachedProperty }] = new RichPropertyReads[DetachedProperty]()(DetachedPropertyFormat)

}
