package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{MultiPropertyValue, MultiRecord, Property}
import play.api.libs.json._

/**
  * Created by johann on 31/05/17.
  */
class MultiRecordReads[P <: Property : Reads] extends Reads[MultiRecord { type Prop = P }] {

  def reads(json: JsValue): JsResult[MultiRecord {type Prop = P}] = {
    val result = for {
      opt <- (JsPath \ "properties").readNullable[Map[P#Key, MultiPropertyValue[P]]].reads(json)
      map = opt match {
        case Some(m) => m
        case _ => Map.empty[P#Key, MultiPropertyValue[P]]
      }
    } yield {
      new MultiRecord {
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

//  private[this] implicit lazy val mapReads: Reads[Map[P#Key, MultiPropertyValue[P]]] = KeyFormat.mapReads[ MultiPropertyValue[P]](multiPropertyValueReads)
  private[this] implicit lazy val mapReads: Reads[Map[P#Key, MultiPropertyValue[P]]] = implicitly[Reads[Seq[MultiPropertyValue[P]]]].map { seq =>
    (for {
      prop <- seq
    } yield prop.key -> prop).toMap
  }

  private[this] implicit lazy val multiPropertyValueReads: MultiPropertyValueReads[P] = new MultiPropertyValueReads[P]

}
