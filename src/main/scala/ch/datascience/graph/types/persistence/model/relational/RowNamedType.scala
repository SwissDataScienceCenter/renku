package ch.datascience.graph.types.persistence.model.relational

import java.util.UUID

import ch.datascience.graph.types.persistence.model.EntityType

/**
  * Created by johann on 12/05/17.
  */
case class RowNamedType(
  id: UUID,
  graphDomainId: UUID,
  name: String
) extends RowAbstractEntity {

  final override val entityType: EntityType = EntityType.NamedType

}
