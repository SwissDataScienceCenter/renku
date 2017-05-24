package ch.datascience.graph.scope.persistence.json

import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.types.json.PropertyKeyReads
import play.api.libs.json.{JsResult, JsValue, Reads}

/**
  * Created by johann on 24/05/17.
  */
class FetchPropertiesForResponseReads[Key : Reads] extends Reads[Map[Key, PropertyKey[Key]]] {

  def reads(json: JsValue): JsResult[Map[Key, PropertyKey[Key]]] = seqReads.reads(json) map { seq =>
    val withKey = for {
      property <- seq
    } yield property.key -> property
    withKey.toMap
  }

  private[this] lazy val seqReads = implicitly[Reads[Seq[PropertyKey[Key]]]]

  private[this] implicit lazy val propertyKeyReads: Reads[PropertyKey[Key]] = new PropertyKeyReads[Key]

}
