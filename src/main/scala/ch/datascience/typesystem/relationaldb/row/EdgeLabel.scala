package ch.datascience.typesystem.relationaldb.row

import java.util.UUID

import ch.datascience.typesystem.model.EntityType
import ch.datascience.typesystem.model.relational.AbstractEntity
import org.janusgraph.core.Multiplicity

/**
  * Created by johann on 16/03/17.
  */
case class EdgeLabel(id: UUID,
                     graphDomainId: UUID,
                     name: String,
                     multiplicity: Multiplicity = Multiplicity.SIMPLE)
  extends AbstractEntity {

  override val entityType: EntityType = EntityType.EdgeLabel

}
