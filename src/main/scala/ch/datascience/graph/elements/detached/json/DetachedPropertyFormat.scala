package ch.datascience.graph.elements.detached.json

import ch.datascience.graph.elements.detached.DetachedProperty
import ch.datascience.graph.elements.json.PropertyFormat
import play.api.libs.json.{Format, JsResult, JsValue}

/**
  * Created by johann on 31/05/17.
  */
object DetachedPropertyFormat extends Format[DetachedProperty] {

  def writes(prop: DetachedProperty): JsValue = PropertyFormat.writes(prop)

  def reads(json: JsValue): JsResult[DetachedProperty] = for {
    prop <- PropertyFormat.reads(json)
  } yield DetachedProperty(prop.key, prop.value)

}
