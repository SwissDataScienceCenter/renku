package ch.datascience.graph.types.persistence.relationaldb

import java.time.Instant
import java.util.UUID

import ch.datascience.graph.types.persistence.model.relational.{RowEntity, RowState}
import ch.datascience.graph.types.persistence.model.EntityState
import slick.lifted.{CompiledFunction, ForeignKeyQuery, Index, ProvenShape}

/**
  * Created by johann on 20/03/17.
  */
trait StateComponent { this: JdbcProfileComponent with SchemasComponent with ImplicitsComponent with EntityComponent =>

  import profile.api._

  class States(tag: Tag) extends Table[RowState](tag, "STATES") {

    // Columns
    def id: Rep[Long] = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def entityId: Rep[UUID] = column[UUID]("ENTITY_UUID")

    def state: Rep[EntityState] = column[EntityState]("STATE")

    def timestamp: Rep[Instant] = column[Instant]("STATE_TIMESTAMP")

    // Indexes
    def entityIdIdx: Index = index("IDX_STATES_ENTITY_UUID", entityId)

    // Foreign keys
    def entity: ForeignKeyQuery[Entities, RowEntity] =
      foreignKey("STATES_FK_ENTITIES", entityId, entities)(_.id)

    // *
    def * : ProvenShape[RowState] = (id.?, entityId, state, timestamp) <> (RowState.tupled, RowState.unapply)

  }

  object states extends TableQuery(new States(_)) {

    val findByEntityId: CompiledFunction[Rep[UUID] => Query[States, RowState, Seq], Rep[UUID], UUID, Query[States, RowState, Seq], Seq[RowState]] =
      this.findBy(_.entityId)

  }

  _schemas += states.schema

}
