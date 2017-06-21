package ch.datascience.graph.types.persistence.model.json

import java.util.UUID

import ch.datascience.graph.naming.json.{NameFormat, NamespaceFormat}
import ch.datascience.graph.types.json.{CardinalityFormat, DataTypeFormat}
import ch.datascience.graph.types.persistence.model.{GraphDomain, RichPropertyKey, SystemPropertyKey}
import ch.datascience.graph.types.{Cardinality, DataType}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath}

/**
  * Created by johann on 13/06/17.
  */
object SystemPropertyKeyMappers {

  lazy val SystemPropertyKeyFormat: Format[SystemPropertyKey] = (
    (JsPath \ "id").format[UUID](UUIDFormat) and
      (JsPath \ "name").format[String](NameFormat) and
      (JsPath \ "data_type").format[DataType](DataTypeFormat) and
      (JsPath \ "cardinality").format[Cardinality](CardinalityFormat)
  )(SystemPropertyKey.apply, unlift(SystemPropertyKey.unapply))

  lazy val SystemPropertyKeyRequestFormat: Format[(String, DataType, Cardinality)] = (
    (JsPath \ "name").format[String](NameFormat) and
      (JsPath \ "data_type").format[DataType](DataTypeFormat) and
      (JsPath \ "cardinality").format[Cardinality](CardinalityFormat)
  )(Tuple3.apply, unlift(Tuple3.unapply))

}
