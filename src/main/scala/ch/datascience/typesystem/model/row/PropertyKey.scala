package ch.datascience.typesystem.model.row

import java.util.UUID

import ch.datascience.typesystem.model.{DataType, EntityType}
import org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality

/**
  * Created by johann on 16/03/17.
  */
case class PropertyKey(id: UUID,
                       graphDomainId: UUID,
                       name: String,
                       dataType: DataType = DataType.STRING,
                       cardinality: Cardinality = Cardinality.single)
  extends AbstractEntity {

  override val entityType: EntityType = EntityType.PROPERTY_KEY

}
