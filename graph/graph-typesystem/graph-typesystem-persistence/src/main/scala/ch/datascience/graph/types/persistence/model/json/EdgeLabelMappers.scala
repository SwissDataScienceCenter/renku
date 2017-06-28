package ch.datascience.graph.types.persistence.model.json

import java.util.UUID

import ch.datascience.graph.naming.json.{NameFormat, NamespaceFormat}
import ch.datascience.graph.types.Multiplicity
import ch.datascience.graph.types.json.MultiplicityFormat
import ch.datascience.graph.types.persistence.model.{GraphDomain, RichEdgeLabel}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath}

/**
  * Created by johann on 13/06/17.
  */
object EdgeLabelMappers {

  lazy val EdgeLabelFormat: Format[RichEdgeLabel] = (
    (JsPath \ "id").format[UUID](UUIDFormat) and
      (JsPath \ "graph_domain").format[GraphDomain](GraphDomainFormat) and
      (JsPath \ "name").format[String](NameFormat) and
      (JsPath \ "multiplicity").format[Multiplicity](MultiplicityFormat)
  )(RichEdgeLabel.apply, unlift(RichEdgeLabel.unapply))

  lazy val EdgeLabelRequestFormat: Format[(String, String, Multiplicity)] = (
    (JsPath \ "namespace").format[String](NamespaceFormat) and
      (JsPath \ "name").format[String](NameFormat) and
      (JsPath \ "multiplicity").format[Multiplicity](MultiplicityFormat)
    )(Tuple3.apply, unlift(Tuple3.unapply))

}
