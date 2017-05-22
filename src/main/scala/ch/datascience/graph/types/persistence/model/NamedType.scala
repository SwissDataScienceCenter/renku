package ch.datascience.graph.types.persistence.model

import java.util.UUID

import ch.datascience.graph.naming.{Name, NamespaceAndName}
import ch.datascience.graph.types.persistence.model.relational.{RowNamedType, RowPropertyKey}
import ch.datascience.graph.types.{NamedType => NamedTypeBase}

/**
  * Created by johann on 12/05/17.
  */
case class NamedType(
  id: UUID,
  graphDomain: GraphDomain,
  name: String,
  superTypesMap: Map[NamespaceAndName, RowNamedType],
//  propertiesMap: Map[NamespaceAndName, RowPropertyKey]
  propertiesMap: Map[NamespaceAndName, PropertyKey]
) extends AbstractEntity[RowNamedType]
  with NamedTypeBase[NamespaceAndName, NamespaceAndName] {
  Name(name)
//  require(NamespaceAndName.nameIsValid(name), s"Invalid name: '$name' (Pattern: ${NamespaceAndName.namePattern})")

  def namespace: String = graphDomain.namespace

  def key: NamespaceAndName = NamespaceAndName(namespace, name)

  def superTypes: Set[NamespaceAndName] = superTypesMap.keySet

  def properties: Set[NamespaceAndName] = propertiesMap.keySet

  def toRow: RowNamedType = RowNamedType(id, graphDomain.id, name)

  final override val entityType: EntityType = EntityType.NamedType

}
