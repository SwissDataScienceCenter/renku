package ch.datascience.graph.types.impl

import ch.datascience.graph.types.NamedType

/**
  * Created by johann on 12/05/17.
  */
private[types] final case class ImplNamedType(
  typeId: NamedType#TypeId,
  superTypes: Set[NamedType#TypeId],
  properties: Set[NamedType#Key]
) extends NamedType {

  override def toString: String = s"NamedType($typeId, $superTypes, $properties)"

}
