package ch.datascience.graph.types.persistence.model.json

import java.util.UUID

import ch.datascience.graph.naming.json.{NameFormat, NamespaceFormat}
import ch.datascience.graph.types.json.{CardinalityFormat, DataTypeFormat}
import ch.datascience.graph.types.persistence.model.{GraphDomain, RichPropertyKey}
import ch.datascience.graph.types.{Cardinality, DataType}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath}

/**
  * Created by johann on 13/06/17.
  */
object PropertyKeyMappers {

  lazy val PropertyKeyFormat: Format[RichPropertyKey] = (
    (JsPath \ "id").format[UUID](UUIDFormat) and
      (JsPath \ "graph_domain").format[GraphDomain](GraphDomainFormat) and
      (JsPath \ "name").format[String](NameFormat) and
      (JsPath \ "data_type").format[DataType](DataTypeFormat) and
      (JsPath \ "cardinality").format[Cardinality](CardinalityFormat)
  )(RichPropertyKey.apply, unlift(RichPropertyKey.unapply))

  lazy val PropertyKeyRequestFormat: Format[(String, String, DataType, Cardinality)] = (
    (JsPath \ "namespace").format[String](NamespaceFormat) and
      (JsPath \ "name").format[String](NameFormat) and
      (JsPath \ "data_type").format[DataType](DataTypeFormat) and
      (JsPath \ "cardinality").format[Cardinality](CardinalityFormat)
  )(Tuple4.apply, unlift(Tuple4.unapply))

}
