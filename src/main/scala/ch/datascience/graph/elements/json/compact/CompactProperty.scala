package ch.datascience.graph.elements.json.compact

import play.api.libs.json.{JsObject, JsPath, JsString, Reads}

/**
  * Created by johann on 01/06/17.
  */
object CompactProperty {

  lazy val deflate: Reads[JsObject] = (JsPath \ "key").json.prune

  def inflate(key: String): Reads[JsObject] = JsPath.json.update(
    (JsPath \ "key").json.put(JsString(key))
  )

  lazy val deflateDataType: Reads[JsObject] = deflate andThen (JsPath \ "value" \ "data_type").json.prune

  def inflateDataType(key: String, dataType: String): Reads[JsObject] = inflate(key) andThen JsPath.json.update(
    (JsPath \ "value" \ "data_type").json.put(JsString(dataType))
  )

}
