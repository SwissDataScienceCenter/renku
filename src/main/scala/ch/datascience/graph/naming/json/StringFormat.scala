package ch.datascience.graph.naming.json

import play.api.libs.json.{Format, JsResult, JsString}

/**
  * Created by johann on 24/05/17.
  */
trait StringFormat[Key] extends Format[Key] with StringReads[Key] with StringWrites[Key]

object StringFormat {

  def apply[Key](r: StringReads[Key], w: StringWrites[Key]): StringFormat[Key] = new StringFormat[Key] {
    def reads(jsString: JsString): JsResult[Key] = r.reads(jsString)
    def writes(key: Key): JsString = w.writes(key)
  }
  
}
