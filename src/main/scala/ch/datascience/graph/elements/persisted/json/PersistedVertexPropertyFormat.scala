package ch.datascience.graph.elements.persisted.json

import ch.datascience.graph.elements.RichProperty
import ch.datascience.graph.elements.json.{RichPropertyReads, RichPropertyWrites}
import ch.datascience.graph.elements.persisted.{Path, PersistedRecordProperty, PersistedVertexProperty}
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 19/06/17.
  */
object PersistedVertexPropertyFormat extends Format[PersistedVertexProperty] {

  def writes(prop: PersistedVertexProperty): JsValue = writer.writes(prop)

  def reads(json: JsValue): JsResult[PersistedVertexProperty] = reader.reads(json)

  private[this] lazy val writer: Writes[PersistedVertexProperty] = (
    (JsPath \ "id").write[PersistedVertexProperty#Id] and
      (JsPath \ "parent").write[Path](PathMappers.PathFormat) and
      JsPath.write[RichProperty { type Prop = PersistedRecordProperty }](propertyWriter)
    ) { prop => (prop.id, prop.parent, prop) }

  private[this] lazy val reader: Reads[PersistedVertexProperty] = (
    (JsPath \ "id").read[PersistedVertexProperty#Id] and
     (JsPath \ "parent").read[Path](PathMappers.PathFormat) and
      JsPath.read[RichProperty { type Prop = PersistedRecordProperty }](propertyReader)
    ) { (id, parent, prop) => PersistedVertexProperty(id, parent, prop.key, prop.value, prop.properties) }

  private[this] lazy val propertyWriter: Writes[RichProperty { type Prop = PersistedRecordProperty }] = new RichPropertyWrites[PersistedRecordProperty]()(PersistedRecordPropertyFormat)

  private[this] lazy val propertyReader: Reads[RichProperty { type Prop = PersistedRecordProperty }] = new RichPropertyReads[PersistedRecordProperty]()(PersistedRecordPropertyFormat)

}
