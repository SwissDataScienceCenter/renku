package ch.datascience.typesystem.model.relational

import java.util.UUID

import ch.datascience.typesystem.model.{Cardinality, DataType, EntityType}

/**
  * Created by johann on 16/03/17.
  */
case class PropertyKey(id: UUID,
                       graphDomainId: UUID,
                       name: String,
                       dataType: DataType,
                       cardinality: Cardinality)
  extends PropertyKeyBase
