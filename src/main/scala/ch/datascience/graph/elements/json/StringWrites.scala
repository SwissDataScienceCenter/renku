package ch.datascience.graph.elements.json

import play.api.libs.json.{JsString, JsValue, Writes}

/**
  * Created by johann on 24/05/17.
  */
trait StringWrites[Key] extends Writes[Key] { self =>

  def writes(key: Key): JsString

  def mapWrites[Value : Writes]: Writes[Map[Key, Value]] = Writes[Map[Key, Value]] { map =>
    val stringMap = for {
      (key, value) <- map
    } yield self.writes(key).value -> value
    implicitly[Writes[Map[String, Value]]].writes(stringMap)
  }

}
