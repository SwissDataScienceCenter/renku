package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{Property, Record}
import play.api.data.validation.ValidationError
import play.api.libs.json._

/**
  * Created by johann on 30/05/17.
  */
class RecordReads[P <: Property : Reads] extends Reads[Record { type Prop = P }] {

  def reads(json: JsValue): JsResult[Record {type Prop = P}] = {
    val result = for {
      opt <- (JsPath \ "properties").readNullable[Map[P#Key, P]].reads(json)
      map = opt match {
        case Some(m) => m
        case _ => Map.empty[P#Key, P]
      }
    } yield {
      new Record {
        type Prop = P
        def properties: Properties = map
      }
    }

    // Repath
    result match {
      case JsSuccess(x, _) => JsSuccess(x)
      case _ => result
    }
  }

//  private[this] implicit lazy val mapReads: Reads[Map[P#Key, P]] = KeyFormat.mapReads[P]
  private[this] implicit lazy val mapReads: Reads[Map[P#Key, P]] = implicitly[Reads[Seq[P]]].map { seq =>
    (for {
      prop <- seq
    } yield prop.key -> prop).toMap
  }

}
