package ch.datascience.typesystem.model.table

import java.time.Instant
import java.util.UUID

import ch.datascience.typesystem.model.EntityState
import ch.datascience.typesystem.model.row.{Entity, State, Transition}
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

/**
  * Created by johann on 20/03/17.
  */
trait TransitionComponent { this: JdbcProfileComponent with StateComponent with EntityComponent =>

  import profile.api._

  class Transitions(tag: Tag) extends Table[Transition](tag, "TRANSITIONS") {

    // Columns
    def entityId: Rep[UUID] = column[UUID]("ENTITY_UUID")

    def from: Rep[Long] = column[Long]("FROM")

    def toState: Rep[EntityState] = column[EntityState]("TO_STATE")

    def toTimestamp: Rep[Instant] = column[Instant]("TO_TIMESTAMP")

    // Indexes
    def pk: PrimaryKey = primaryKey("IDX_TRANSITIONS", (entityId, from))

    // Foreign keys
    def entity: ForeignKeyQuery[Entities, Entity] =
      foreignKey("TRANSITIONS_FK_ENTITIES", entityId, entities)(_.id)

    def fromState: ForeignKeyQuery[States, State] =
      foreignKey("TRANSITIONS_FK_STATES", from, states)(_.id)

    // *
    def * : ProvenShape[Transition] = (entityId, from, toState, toTimestamp) <> (Transition.tupled, Transition.unapply)

  }

  val transitions: TableQuery[Transitions] = TableQuery[Transitions]

  /*
  object transitions extends TableQuery(new Transitions(_)) {

    def makeTransition(entityId: UUID, from: Long, toState: EntityState): DBIO[Int] = {
      val toTimestamp = Instant.now()
      val transition = Transition(entityId, from, toState, toTimestamp)
      val insertTransition = this += transition
      val insertState = states += State(None, entityId, toState, toTimestamp)
      val deleteTransition = this.filter(_.entityId === entityId).filter(_.from === from).delete
      insertTransition andThen insertState andThen deleteTransition
    }

  }
  */

}
