package ch.datascience.graph.types.json

import ch.datascience.graph.types.NamedType
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Writes}

/**
  * Created by johann on 17/05/17.
  */
class NamedTypeWrites[TypeKey : Writes, PropKey : Writes] extends Writes[NamedType[TypeKey, PropKey]] {

  def writes(namedType: NamedType[TypeKey, PropKey]): JsValue = self.writes(namedType)

  private[this] lazy val self: Writes[NamedType[TypeKey, PropKey]] = makeSelf

  private[this] def makeSelf: Writes[NamedType[TypeKey, PropKey]] = (
    (JsPath \ "key").write[TypeKey] and
      (JsPath \ "super_types").write[Traversable[TypeKey]] and
      (JsPath \ "properties").write[Traversable[PropKey]]
    ).apply(unlift(NamedType.unapply[TypeKey, PropKey]))

}
