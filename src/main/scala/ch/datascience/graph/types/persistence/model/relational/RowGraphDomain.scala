package ch.datascience.graph.types.persistence.model.relational

import java.util.UUID

import ch.datascience.graph.types.persistence.model.EntityType

/**
  * Created by johann on 09/05/17.
  */
case class RowGraphDomain(id: UUID, namespace: String) extends RowAbstractEntity {

  final override val entityType: EntityType = EntityType.GraphDomain

}
