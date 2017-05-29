package ch.datascience.graph.types.json

import ch.datascience.graph.types.{Cardinality, DataType, PropertyKey}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsResult, JsValue, Reads}

/**
  * Created by johann on 17/05/17.
  */
class PropertyKeyReads(implicit r: Reads[PropertyKey#Key]) extends Reads[PropertyKey] {

  override def reads(json: JsValue): JsResult[PropertyKey] = self.reads(json)

  private[this] lazy val self: Reads[PropertyKey] = makeSelf

  private[this] def makeSelf: Reads[PropertyKey] = (
    (JsPath \ "key").read[PropertyKey#Key](r) and
      (JsPath \ "dataType").read[DataType](DataTypeReads) and
      (JsPath \ "cardinality").read[Cardinality](CardinalityReads)
    )(PropertyKey.apply _)

}
