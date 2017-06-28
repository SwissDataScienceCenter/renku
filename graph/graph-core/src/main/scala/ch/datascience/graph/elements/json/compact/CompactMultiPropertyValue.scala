package ch.datascience.graph.elements.json.compact

import play.api.libs.json._

/**
  * Created by johann on 01/06/17.
  */
object CompactMultiPropertyValue {

  lazy val deflate: Reads[JsObject] = (JsPath \ "key").json.prune andThen (JsPath \ "values").json.update(
    Reads.of[JsArray].map { case JsArray(arr) =>
      val res = for {
        js <- arr
      } yield js.transform(CompactProperty.deflateDataType).get
      JsArray(res)
    }
  )

  def inflate(key: String): Reads[JsObject] = (
    JsPath.json.update(
      (JsPath \ "key").json.put(JsString(key))
    )
    andThen
    (JsPath \ "data_type").read[String].flatMap { dataType =>
      (JsPath \ "values").json.update(
        Reads.of[JsArray].map { case JsArray(arr) =>
          val res = for {
            js <- arr
          } yield js.transform(CompactProperty.inflateDataType(key, dataType)).get
          JsArray(res)
        }
      )
    }
  )

}
