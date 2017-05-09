package ch.datascience.graph.types.persistence.model.relational

import java.util.UUID

import ch.datascience.graph.types.{Cardinality, DataType}
import ch.datascience.graph.types.persistence.model.EntityType

/**
  * Created by johann on 16/03/17.
  */
case class RowPropertyKey(
  id: UUID,
  graphDomainId: UUID,
  name: String,
  dataType: DataType,
  cardinality: Cardinality
) extends RowAbstractEntity {

  final override val entityType: EntityType = EntityType.PropertyKey

}
