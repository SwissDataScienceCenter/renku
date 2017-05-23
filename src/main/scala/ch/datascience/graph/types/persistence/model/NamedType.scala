package ch.datascience.graph.types.persistence.model

import java.util.UUID

import ch.datascience.graph.naming.NamespaceAndName

/**
  * Created by johann on 12/05/17.
  */
case class NamedType(
  id: UUID,
  graphDomainId: UUID,
  name: String
) extends AbstractEntity {

  final override val entityType: EntityType = EntityType.NamedType

  def toRichNamedType(graphDomain: GraphDomain, superTypes: Map[NamespaceAndName, RichNamedType], properties: Map[NamespaceAndName, RichPropertyKey]): RichNamedType = RichNamedType(id, graphDomain, name, superTypes, properties)

}
