package ch.datascience.typesystem.model.row

import java.util.UUID

import ch.datascience.typesystem.{Cardinality, DataType}
import ch.datascience.typesystem.model.EntityType

/**
  * Created by johann on 16/03/17.
  */
case class PropertyKey(id: UUID,
                       graphDomainId: UUID,
                       name: String,
                       dataType: DataType = DataType.String,
                       cardinality: Cardinality = Cardinality.Single)
  extends AbstractEntity {

  override val entityType: EntityType = EntityType.PROPERTY_KEY

}
