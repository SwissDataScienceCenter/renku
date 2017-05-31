package ch.datascience.graph.naming.json

import play.api.libs.json._

/**
  * Created by johann on 24/05/17.
  */
trait StringReads[Key] extends Reads[Key] { self =>

  final def reads(json: JsValue): JsResult[Key] = implicitly[Reads[String]].reads(json).flatMap(str => reads(JsString(str)))

  def reads(jsString: JsString): JsResult[Key]

  def mapReads[Value : Reads]: Reads[Map[Key, Value]] = new Reads[Map[Key, Value]] {
    def reads(json: JsValue): JsResult[Map[Key, Value]] = for {
      map <- implicitly[Reads[Map[String, Value]]].reads(json)
      tMap <- transform(map)
    } yield tMap
  }

  private[this] def transform[Value](map: Map[String, Value]): JsResult[Map[Key, Value]] = for {
    keysMap <- Reads.map[Key](self).reads( jsonKeyMap(map.keys) )
  } yield for {
    (stringKey, value) <- map
  } yield keysMap(stringKey) -> value

  private[this] def jsonKeyMap(stringKeys: Iterable[String]): JsObject = {
    val map = for {
      str <- stringKeys
    } yield str -> JsString(str)

    JsObject(map.toMap)
  }

}
