package models.json

import java.util.UUID

import ch.datascience.graph.types.Multiplicity
import ch.datascience.graph.types.json.MultiplicityFormat
import ch.datascience.graph.types.persistence.model.{GraphDomain, RichEdgeLabel}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

/**
  * Created by johann on 07/06/17.
  */
object EdgeLabelMappers {

  def edgeLabelWrites: Writes[RichEdgeLabel] = (
    (JsPath \ "id").write[UUID] and
      (JsPath \ "graphDomain").write[GraphDomain] and
      (JsPath \ "name").write[String] and
      (JsPath \ "multiplicity").write[Multiplicity](MultiplicityFormat)
    )(unlift(RichEdgeLabel.unapply))

}
