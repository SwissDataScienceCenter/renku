package ch.datascience.graph.elements.json

import ch.datascience.graph.elements._
import ch.datascience.graph.types.json.{CardinalityFormat, DataTypeFormat}
import ch.datascience.graph.types.{Cardinality, DataType}
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 31/05/17.
  */
class MultiPropertyValueReads[P <: Property : Reads] extends Reads[MultiPropertyValue[P]] {

  def reads(json: JsValue): JsResult[MultiPropertyValue[P]] = self.reads(json).flatMap {
    case (key, dataType, cardinality, props) => try {
      cardinality match {
        case Cardinality.Single => JsSuccess(SingleValue[P](props.head))
        case Cardinality.Set => JsSuccess(SetValue[P](props.toList))
        case Cardinality.List => JsSuccess(ListValue[P](props.toList))
      }
    } catch {
      case e: IllegalArgumentException => JsError(e.getMessage)
    }
  }

  private[this] lazy val self: Reads[(P#Key, DataType, Cardinality, Seq[P])] = (
    (JsPath \ "key").read[P#Key](KeyFormat) and
      (JsPath \ "data_type").read[DataType](DataTypeFormat) and
      (JsPath \ "cardinality").read[Cardinality](CardinalityFormat) and
      (JsPath \ "values").read[Seq[P]]
    ) { (key, dataType, cardinality, props) => (key, dataType, cardinality, props) }

}
