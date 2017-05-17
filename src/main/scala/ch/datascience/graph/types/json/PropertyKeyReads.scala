package ch.datascience.graph.types.json

import ch.datascience.graph.types.{Cardinality, DataType, PropertyKey}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsResult, JsValue, Reads}

/**
  * Created by johann on 17/05/17.
  */
class PropertyKeyReads[Key : Reads] extends Reads[PropertyKey[Key]] {

  override def reads(json: JsValue): JsResult[PropertyKey[Key]] = self.reads(json)

  private[this] lazy val self: Reads[PropertyKey[Key]] = makeSelf

  private[this] def makeSelf: Reads[PropertyKey[Key]] = (
    (JsPath \ "key").read[Key] and
      (JsPath \ "dataType").read[DataType](DataTypeReads) and
      (JsPath \ "cardinality").read[Cardinality](CardinalityReads)
    )(PropertyKey.apply[Key] _)

}
