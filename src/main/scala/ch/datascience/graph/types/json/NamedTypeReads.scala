package ch.datascience.graph.types.json

import ch.datascience.graph.types.NamedType
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsResult, JsValue, Reads}

/**
  * Created by johann on 17/05/17.
  */
class NamedTypeReads[TypeKey : Reads, PropKey : Reads] extends Reads[NamedType[TypeKey, PropKey]] {

  override def reads(json: JsValue): JsResult[NamedType[TypeKey, PropKey]] = self.reads(json)

  private[this] lazy val self: Reads[NamedType[TypeKey, PropKey]] = makeSelf

  private[this] def makeSelf: Reads[NamedType[TypeKey, PropKey]] = (
    (JsPath \ "key").read[TypeKey] and
      (JsPath \ "super_types").read[Seq[TypeKey]].map(_.toSet) and
      (JsPath \ "properties").read[Seq[PropKey]].map(_.toSet)
  )(NamedType.apply[TypeKey, PropKey] _)

}
