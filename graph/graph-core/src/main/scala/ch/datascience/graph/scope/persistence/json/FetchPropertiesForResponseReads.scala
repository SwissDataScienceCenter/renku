package ch.datascience.graph.scope.persistence.json

import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.types.json.PropertyKeyFormat
import play.api.libs.json.{JsResult, JsValue, Reads}

/**
  * Created by johann on 24/05/17.
  */
object FetchPropertiesForResponseReads extends Reads[Map[PropertyKey#Key, PropertyKey]] {

  def reads(json: JsValue): JsResult[Map[PropertyKey#Key, PropertyKey]] = seqReads.reads(json) map { seq =>
    val withKey = for {
      property <- seq
    } yield property.key -> property
    withKey.toMap
  }

  private[this] lazy val seqReads = Reads.seq(PropertyKeyFormat)

}
