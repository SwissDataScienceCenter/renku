package models
package json

import java.util.UUID

import ch.datascience.graph.types.persistence.model.GraphDomain
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by johann on 13/04/17.
  */
object GraphDomainMappers {

  def graphDomainWrites: Writes[GraphDomain] = (
    (JsPath \ "id").write[UUID] and
      (JsPath \ "namespace").write[String]
  )(unlift(GraphDomain.unapply))

  def graphDomainReads: Reads[GraphDomain] = (
    (JsPath \ "id").read[UUID] and
      (JsPath \ "namespace").read[String]
  )(GraphDomain.apply _)

}
