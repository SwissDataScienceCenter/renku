package ch.datascience.graph.types.json

import ch.datascience.graph.naming.json.NamespaceAndNameFormat
import ch.datascience.graph.types.NamedType
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by johann on 19/06/17.
  */
object NamedTypeFormat extends Format[NamedType] {

  def writes(namedType: NamedType): JsValue = writer.writes(namedType)

  def reads(json: JsValue): JsResult[NamedType] = reader.reads(json)

  private[this] def writer: Writes[NamedType] = (
    (JsPath \ "key").write[NamedType#TypeId](NamespaceAndNameFormat) and
      (JsPath \ "super_types").write[Traversable[NamedType#TypeId]] and
      (JsPath \ "properties").write[Traversable[NamedType#Key]]
    )(unlift(NamedType.unapply))

  private[this] def reader: Reads[NamedType] = (
    (JsPath \ "key").read[NamedType#TypeId](NamespaceAndNameFormat) and
      (JsPath \ "super_types").readNullable[Traversable[NamedType#TypeId]] and
      (JsPath \ "properties").readNullable[Traversable[NamedType#Key]]
    ){ (key, optST, optP) => {
      NamedType(key, optST.map(_.toSet).getOrElse(Set.empty), optP.map(_.toSet).getOrElse(Set.empty))
    }
  }

}
