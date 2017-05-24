package ch.datascience.graph.scope.persistence.json

import play.api.libs.json.{JsResult, JsValue, Reads}

/**
  * Created by johann on 23/05/17.
  */
class FetchPropertiesForQueryReads[Key : Reads] extends Reads[Set[Key]] {

  def reads(json: JsValue): JsResult[Set[Key]] = for { seq <- seqReads.reads(json) } yield seq.toSet

  private[this] lazy val seqReads = implicitly[Reads[Seq[Key]]]

}
