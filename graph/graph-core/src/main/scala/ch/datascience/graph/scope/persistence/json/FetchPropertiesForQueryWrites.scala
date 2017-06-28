package ch.datascience.graph.scope.persistence.json

import ch.datascience.graph.types.PropertyKey
import play.api.libs.json.{JsValue, Writes}

/**
  * Created by johann on 23/05/17.
  */
class FetchPropertiesForQueryWrites(implicit w: Writes[PropertyKey#Key]) extends Writes[Set[PropertyKey#Key]] {

  def writes(keys: Set[PropertyKey#Key]): JsValue = seqWrites.writes(keys.toSeq)

  private[this] lazy val seqWrites = implicitly[Writes[Seq[PropertyKey#Key]]]

}
