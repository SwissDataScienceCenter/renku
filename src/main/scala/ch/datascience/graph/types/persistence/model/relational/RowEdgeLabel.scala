package ch.datascience.graph.types.persistence.model.relational

import java.util.UUID

import ch.datascience.graph.types.persistence.model.EntityType
import org.janusgraph.core.Multiplicity

/**
  * Created by johann on 09/05/17.
  */
case class RowEdgeLabel(
  id: UUID,
  graphDomainId: UUID,
  name: String,
  multiplicity: Multiplicity = Multiplicity.SIMPLE
) extends RowAbstractEntity {

  final override val entityType: EntityType = EntityType.EdgeLabel

}

