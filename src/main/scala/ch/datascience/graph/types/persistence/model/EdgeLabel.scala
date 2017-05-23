package ch.datascience.graph.types.persistence.model

import java.util.UUID

import org.janusgraph.core.Multiplicity

/**
  * Created by johann on 09/05/17.
  */
case class EdgeLabel(
  id: UUID,
  graphDomainId: UUID,
  name: String,
  multiplicity: Multiplicity = Multiplicity.SIMPLE
) extends AbstractEntity {

  final override val entityType: EntityType = EntityType.EdgeLabel

}

