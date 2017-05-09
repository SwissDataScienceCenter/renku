package ch.datascience.graph.types.persistence.relationaldb

import java.time.Instant
import java.util.UUID

import ch.datascience.graph.types.persistence.model.relational.{RowEntity, RowState, RowTransition}
import ch.datascience.graph.types.persistence.model.EntityState
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

/**
  * Created by johann on 20/03/17.
  */
trait TransitionComponent { this: JdbcProfileComponent with SchemasComponent with ImplicitsComponent with StateComponent with EntityComponent =>

  import profile.api._

  class Transitions(tag: Tag) extends Table[RowTransition](tag, "TRANSITIONS") {

    // Columns
    def entityId: Rep[UUID] = column[UUID]("ENTITY_UUID")

    def from: Rep[Long] = column[Long]("FROM")

    def toState: Rep[EntityState] = column[EntityState]("TO_STATE")

    def toTimestamp: Rep[Instant] = column[Instant]("TO_TIMESTAMP")

    // Indexes
    def pk: PrimaryKey = primaryKey("IDX_TRANSITIONS", (entityId, from))

    // Foreign keys
    def entity: ForeignKeyQuery[Entities, RowEntity] =
      foreignKey("TRANSITIONS_FK_ENTITIES", entityId, entities)(_.id)

    def fromState: ForeignKeyQuery[States, RowState] =
      foreignKey("TRANSITIONS_FK_STATES", from, states)(_.id)

    // *
    def * : ProvenShape[RowTransition] = (entityId, from, toState, toTimestamp) <> (RowTransition.tupled, RowTransition.unapply)

  }

  object transitions extends TableQuery(new Transitions(_))

  _schemas += transitions.schema

}
