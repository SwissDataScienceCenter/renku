package ch.datascience.graph.scope.persistence.json

import ch.datascience.graph.types.PropertyKey
import play.api.libs.json.{JsResult, JsValue, Reads}

/**
  * Created by johann on 23/05/17.
  */
class FetchPropertiesForQueryReads(implicit r: Reads[PropertyKey#Key]) extends Reads[Set[PropertyKey#Key]] {

  def reads(json: JsValue): JsResult[Set[PropertyKey#Key]] = for { seq <- seqReads.reads(json) } yield seq.toSet

  private[this] lazy val seqReads = implicitly[Reads[Seq[PropertyKey#Key]]]

}
