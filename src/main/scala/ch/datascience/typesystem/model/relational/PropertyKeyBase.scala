package ch.datascience.typesystem.model.relational

import java.util.UUID

import ch.datascience.typesystem.model.{Cardinality, DataType, EntityType}

/**
  * Created by johann on 16/03/17.
  */
abstract class PropertyKeyBase extends AbstractEntity {

  val id: UUID
  val graphDomainId: UUID
  val name: String
  val dataType: DataType
  val cardinality: Cardinality

  final override val entityType: EntityType = EntityType.PropertyKey

  final def toRow: PropertyKey = PropertyKey(id, graphDomainId, name, dataType, cardinality)

}
