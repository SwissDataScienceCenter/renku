package ch.datascience.graph.elements.json.compact

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

/**
  * Created by johann on 01/06/17.
  */
object CompactMultiRecord {

  lazy val deflate: Reads[JsObject] = new Reads[JsObject] {

    def reads(json: JsValue): JsResult[JsObject] = {
      val jsObj = json.as[JsObject]
      val keys = (json \ "properties").as[JsObject].keys

      val res = keys.foldLeft[JsObject](jsObj) { (obj, key) =>
        obj.transform(deflateProperty(key)).get
      }

      JsSuccess(res)
    }

    private[this] def deflateProperty(key: String): Reads[JsObject] = (
      (JsPath \ "properties" \ key).json.prune and
        (JsPath \ "properties" \ key).read(CompactMultiPropertyValue.deflate).flatMap { prop =>
          (JsPath \ "properties" \ key).json.put(prop)
        }
    ).reduce

  }

  lazy val inflate: Reads[JsObject] = new Reads[JsObject] {

    def reads(json: JsValue): JsResult[JsObject] = {
      val jsObj = json.as[JsObject]
      val keys = (json \ "properties").as[JsObject].keys

      val res = keys.foldLeft[JsObject](jsObj) { (obj, key) =>
        obj.transform(inflateProperty(key)).get
      }

      JsSuccess(res)
    }

    private[this] def inflateProperty(key: String): Reads[JsObject] = (JsPath \ "properties" \ key).json.update(CompactMultiPropertyValue.inflate(key))

  }

}
