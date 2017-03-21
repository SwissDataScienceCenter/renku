package ch.datascience.typesystem.model

import java.time.Instant

import ch.datascience.typesystem.model.GenericProfile.api._
import org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality
import org.janusgraph.core.Multiplicity
import slick.ast.NumericTypedType

/**
  * Created by johann on 16/03/17.
  */
package object table {

  implicit val entityTypeColumnType: BaseColumnType[EntityType] =
    MappedColumnType.base[EntityType, String](_.name(), EntityType.valueOf)

  implicit val dataTypeColumnType: BaseColumnType[DataType] =
    MappedColumnType.base[DataType, String](_.name(), DataType.valueOf)

  implicit val cardinalityColumnType: BaseColumnType[Cardinality] =
    MappedColumnType.base[Cardinality, String](_.name(), Cardinality.valueOf)

  implicit val multiplicityColumnType: BaseColumnType[Multiplicity] =
    MappedColumnType.base[Multiplicity, String](_.name(), Multiplicity.valueOf)

  implicit val entityStateColumnType: BaseColumnType[EntityState] =
    MappedColumnType.base[EntityState, String](_.name(), EntityState.valueOf)

  implicit val customTimestampColumnType: BaseColumnType[Instant] =
    MappedColumnType.base[Instant, Long](_.toEpochMilli, Instant.ofEpochMilli)

}
