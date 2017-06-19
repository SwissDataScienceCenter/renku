package ch.datascience.graph.elements.persisted.json

import ch.datascience.graph.elements.Property
import ch.datascience.graph.elements.json.PropertyFormat
import ch.datascience.graph.elements.persisted.{Path, PersistedRecordProperty}
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 13/06/17.
  */
object PersistedRecordPropertyFormat extends Format[PersistedRecordProperty] {

  def writes(prop: PersistedRecordProperty): JsValue = writer.writes(prop)

  def reads(json: JsValue): JsResult[PersistedRecordProperty] = reader.reads(json)

  private[this] lazy val writer: Writes[PersistedRecordProperty] = (
    (JsPath \ "parent").write[Path](PathMappers.PathFormat) and
      JsPath.write[Property](PropertyFormat)
  ) { prop => (prop.parent, prop) }

  private[this] lazy val reader: Reads[PersistedRecordProperty] = (
    (JsPath \ "parent").read[Path](PathMappers.PathFormat) and
      JsPath.read[Property](PropertyFormat)
    ) { (parent, prop) => PersistedRecordProperty(parent, prop.key, prop.value) }

}
