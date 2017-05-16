package ch.datascience.graph.types.impl

import ch.datascience.graph.types.NamedType

/**
  * Created by johann on 12/05/17.
  */
private[types] final case class ImplNamedType[TypeKey, PropKey](
  key: TypeKey,
  superTypes: Set[TypeKey],
  properties: Set[PropKey]
) extends NamedType[TypeKey, PropKey] {

  override def toString: String = s"NamedType($key, $superTypes, $properties)"

}
