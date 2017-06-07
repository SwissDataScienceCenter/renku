package ch.datascience.graph.types.persistence.relationaldb

import java.time.Instant

import ch.datascience.graph.types.{Cardinality, DataType, Multiplicity}
import ch.datascience.graph.types.persistence.model.{EntityState, EntityType}

/**
  * Created by johann on 13/04/17.
  */
trait ImplicitsComponent { this: JdbcProfileComponent =>

  import profile.api._

  implicit val entityTypeColumnType: BaseColumnType[EntityType] =
    MappedColumnType.base[EntityType, String](_.name, EntityType.valueOf)

  implicit val dataTypeColumnType: BaseColumnType[DataType] =
    MappedColumnType.base[DataType, String](_.name, DataType.valueOf)

  implicit val cardinalityColumnType: BaseColumnType[Cardinality] =
    MappedColumnType.base[Cardinality, String](_.name, Cardinality.valueOf)

  implicit val multiplicityColumnType: BaseColumnType[Multiplicity] =
    MappedColumnType.base[Multiplicity, String](_.name, Multiplicity.valueOf)

  implicit val entityStateColumnType: BaseColumnType[EntityState] =
    MappedColumnType.base[EntityState, String](_.name, EntityState.valueOf)

  implicit val customTimestampColumnType: BaseColumnType[Instant] =
    MappedColumnType.base[Instant, Long](_.toEpochMilli, Instant.ofEpochMilli)

}
