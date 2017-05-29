package ch.datascience.graph.types.json

import ch.datascience.graph.types.NamedType
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsResult, JsValue, Reads}

/**
  * Created by johann on 17/05/17.
  */
class NamedTypeReads(implicit rt: Reads[NamedType#TypeId], rk: Reads[NamedType#Key]) extends Reads[NamedType] {

  override def reads(json: JsValue): JsResult[NamedType] = self.reads(json)

  private[this] lazy val self: Reads[NamedType] = makeSelf

  private[this] def makeSelf: Reads[NamedType] = (
    (JsPath \ "key").read[NamedType#TypeId](rt) and
      (JsPath \ "super_types").read[Seq[NamedType#TypeId]].map(_.toSet) and
      (JsPath \ "properties").read[Seq[NamedType#Key]].map(_.toSet)
  )(NamedType.apply _)

  private[this] implicit lazy val seqReads: Reads[Seq[NamedType#TypeId]] = Reads.seq(rt)

}
