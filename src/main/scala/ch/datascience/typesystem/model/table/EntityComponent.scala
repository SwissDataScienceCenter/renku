package ch.datascience.typesystem.model.table

import java.time.Instant
import java.util.UUID

import ch.datascience.typesystem.model.{EntityState, EntityType}
import ch.datascience.typesystem.model.row.{Entity, State, Transition}
import slick.jdbc.JdbcProfile
import slick.lifted.{CompiledFunction, ProvenShape}

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 17/03/17.
  */
trait EntityComponent { this: JdbcProfileComponent with StateComponent with TransitionComponent =>

  import profile.api._

  class Entities(tag: Tag) extends Table[Entity](tag, "ENTITIES") {

    // Columns
    def id: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)

    def entityType: Rep[EntityType] = column[EntityType]("ENTITY_TYPE")

    // *
    def * : ProvenShape[Entity] = (id, entityType) <> (Entity.tupled, Entity.unapply)

  }

  object entities extends TableQuery(new Entities(_)) {

    val findById: CompiledFunction[Rep[UUID] => Query[Entities, Entity, Seq], Rep[UUID], UUID, Query[Entities, Entity, Seq], Seq[Entity]] =
      this.findBy(_.id)

    def defaultValidator(from: EntityState, to : EntityState): Boolean = true

    def makeTransition(entity: Entity, toState: EntityState, transitionValidator: (EntityState, EntityState) => Boolean = defaultValidator)(implicit ec: ExecutionContext): DBIO[Int] = {
      val toTimestamp = Instant.now()
      val selectCurrentState = (for {
        s <- states.sortBy(_.timestamp.desc)
        e <- s.entity if e.id === entity.id
      } yield s.id).take(1).result.headOption
      // TODO: validate
      def insertTransition(from: Long): DBIO[Int] = {
        val transition = Transition(entity.id, from, toState, toTimestamp)
        transitions += transition
      }
      val insertState = states += State(None, entity.id, toState, toTimestamp)
      def deleteTransition(from: Long): DBIO[Int] = transitions.filter(_.entityId === entity.id).filter(_.from === from).delete
      (selectCurrentState flatMap { x =>
        val from = x.get
        insertTransition(from) andThen insertState andThen deleteTransition(from)
      }).transactionally
    }

  }

}
