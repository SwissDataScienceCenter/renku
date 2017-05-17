package ch.datascience.graph.types.persistence.model

import java.util.UUID

import ch.datascience.graph.{Name, NamespaceAndName}
import ch.datascience.graph.types.persistence.model.relational.RowNamedType

/**
  * Created by johann on 12/05/17.
  */
case class NamedTypeBasic(
  id: UUID,
  graphDomain: GraphDomain,
  name: String
) extends AbstractEntity[RowNamedType] {
  Name(name)
//  require(NamespaceAndName.nameIsValid(name), s"Invalid name: '$name' (Pattern: ${NamespaceAndName.namePattern})")

  def namespace: String = graphDomain.namespace

  def key: NamespaceAndName = NamespaceAndName(namespace, name)

  def toRow: RowNamedType = RowNamedType(id, graphDomain.id, name)

  final override val entityType: EntityType = EntityType.NamedType

}
