package models
package json

import java.util.UUID

import ch.datascience.graph.types.{Cardinality, DataType}
import ch.datascience.graph.types.persistence.model.{GraphDomain, PropertyKey}
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by johann on 26/04/17.
  */
object PropertyKeyMappers {

  def propertyKeyWrites: Writes[PropertyKey] = (
    (JsPath \ "id").write[UUID] and
      (JsPath \ "graphDomain").write[GraphDomain] and
      (JsPath \ "name").write[String] and
      (JsPath \ "dataType").write[DataType] and
      (JsPath \ "cardinality").write[Cardinality]
    )(unlift(PropertyKey.unapply))

}
