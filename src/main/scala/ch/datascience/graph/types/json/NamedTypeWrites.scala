package ch.datascience.graph.types.json

import ch.datascience.graph.types.NamedType
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Writes}

/**
  * Created by johann on 17/05/17.
  */
class NamedTypeWrites extends Writes[NamedType] {

  def writes(namedType: NamedType): JsValue = self.writes(namedType)

  private[this] lazy val self: Writes[NamedType] = makeSelf

  private[this] def makeSelf: Writes[NamedType] = (
    (JsPath \ "key").write[NamedType#TypeId] and
      (JsPath \ "super_types").write[Traversable[NamedType#TypeId]] and
      (JsPath \ "properties").write[Traversable[NamedType#Key]]
    ).apply(unlift(NamedType.unapply))

}
