package ch.datascience.graph.elements.json

import ch.datascience.graph.elements._
import ch.datascience.graph.types.json.{CardinalityFormat, DataTypeFormat}
import ch.datascience.graph.types.{Cardinality, DataType}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsResult, JsValue, Reads}

/**
  * Created by johann on 31/05/17.
  */
class MultiPropertyValueReads[P <: Property : Reads] extends Reads[MultiPropertyValue[P]] {

  def reads(json: JsValue): JsResult[MultiPropertyValue[P]] = self.reads(json)

  private[this] lazy val self: Reads[MultiPropertyValue[P]] = (
    (JsPath \ "key").read[P#Key](KeyFormat) and
      (JsPath \ "data_type").read[DataType](DataTypeFormat) and
      (JsPath \ "cardinality").read[Cardinality](CardinalityFormat) and
      (JsPath \ "values").read[Iterable[P]]
    ) { (key, dataType, cardinality, props) =>
    cardinality match {
      case Cardinality.Single => SingleValue[P](props.head)
      case Cardinality.Set => SetValue[P](props.toList)
      case Cardinality.List => ListValue[P](props.toList)
    }
  }

}
