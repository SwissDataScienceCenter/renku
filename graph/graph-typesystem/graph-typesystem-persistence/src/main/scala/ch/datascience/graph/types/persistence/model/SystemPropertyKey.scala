package ch.datascience.graph.types.persistence.model

import java.util.UUID

import ch.datascience.graph.types.{Cardinality, DataType}

/**
  * Created by johann on 16/03/17.
  */
case class SystemPropertyKey(
  id: UUID,
  name: String,
  dataType: DataType,
  cardinality: Cardinality
) extends AbstractEntity
  with RichAbstractEntity[SystemPropertyKey] {

  final override val entityType: EntityType = EntityType.SystemPropertyKey

}
