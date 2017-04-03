package ch.datascience.typesystem.model

import java.time.Instant

import org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality
import org.janusgraph.core.Multiplicity
import slick.jdbc.JdbcProfile

/**
  * Created by johann on 16/03/17.
  */
package object table {

  object profile extends JdbcProfile

  import profile.api._

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
