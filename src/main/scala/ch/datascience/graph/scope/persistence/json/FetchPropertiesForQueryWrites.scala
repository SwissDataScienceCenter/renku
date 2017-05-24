package ch.datascience.graph.scope.persistence.json

import play.api.libs.json.{JsValue, Writes}

/**
  * Created by johann on 23/05/17.
  */
class FetchPropertiesForQueryWrites[Key : Writes] extends Writes[Set[Key]] {

  def writes(keys: Set[Key]): JsValue = seqWrites.writes(keys.toSeq)

  private[this] lazy val seqWrites = implicitly[Writes[Seq[Key]]]

}
