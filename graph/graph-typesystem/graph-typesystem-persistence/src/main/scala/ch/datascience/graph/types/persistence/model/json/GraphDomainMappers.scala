package ch.datascience.graph.types.persistence.model.json

import java.util.UUID

import ch.datascience.graph.naming.json.NamespaceFormat
import ch.datascience.graph.types.persistence.model.GraphDomain
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 13/06/17.
  */
object GraphDomainMappers {

  lazy val GraphDomainFormat: Format[GraphDomain] = (
    (JsPath \ "id").format[UUID](UUIDFormat) and
      (JsPath \ "namespace").format[String](NamespaceFormat)
  )(GraphDomain.apply, unlift(GraphDomain.unapply))

  lazy val GraphDomainRequestFormat: Format[String] = Format(graphDomainRequestReads, (JsPath \ "namespace").write[String])

  private[this] def graphDomainRequestReads: Reads[String] = Reads { json =>
    (JsPath \ "namespace").read[String](NamespaceFormat).reads(json) match {
      case JsSuccess(ns, _) => JsSuccess(ns)
      case e: JsError => e
    }
  }

}
