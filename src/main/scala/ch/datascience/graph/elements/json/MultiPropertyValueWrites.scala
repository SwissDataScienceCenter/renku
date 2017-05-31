package ch.datascience.graph.elements.json

import ch.datascience.graph.elements.{MultiPropertyValue, Property}
import ch.datascience.graph.types.json.{CardinalityFormat, DataTypeFormat}
import ch.datascience.graph.types.{Cardinality, DataType}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Writes}

/**
  * Created by johann on 31/05/17.
  */
class MultiPropertyValueWrites[P <: Property : Writes] extends Writes[MultiPropertyValue[P]] {

  def writes(value: MultiPropertyValue[P]): JsValue = self.writes(value)

  private[this] lazy val self: Writes[MultiPropertyValue[P]] = (
    (JsPath \ "key").write[P#Key](KeyFormat) and
      (JsPath \ "data_type").write[DataType](DataTypeFormat) and
      (JsPath \ "cardinality").write[Cardinality](CardinalityFormat) and
      (JsPath \ "values").write[Iterable[P]]
  ) { value => (value.key, value.dataType, value.cardinality, value.asIterable) }

}
