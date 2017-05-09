package ch.datascience.graph.types.persistence.relationaldb

import java.time.Instant
import java.util.UUID

import ch.datascience.graph.types.persistence.model.{EntityState, EntityType}
import ch.datascience.graph.types.persistence.model.relational.{RowEntity, RowState, RowTransition}
import slick.lifted.{CompiledFunction, ProvenShape}

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 17/03/17.
  */
trait EntityComponent { this: JdbcProfileComponent with SchemasComponent with ImplicitsComponent with StateComponent with TransitionComponent =>

  import profile.api._

  class Entities(tag: Tag) extends Table[RowEntity](tag, "ENTITIES") {

    // Columns
    def id: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)

    def entityType: Rep[EntityType] = column[EntityType]("ENTITY_TYPE")

    // *
    def * : ProvenShape[RowEntity] = (id, entityType) <> (RowEntity.tupled, RowEntity.unapply)

  }

  object entities extends TableQuery(new Entities(_)) {

    val findById: CompiledFunction[Rep[UUID] => Query[Entities, RowEntity, Seq], Rep[UUID], UUID, Query[Entities, RowEntity, Seq], Seq[RowEntity]] =
      this.findBy(_.id)

    def defaultValidator(from: EntityState, to : EntityState): Boolean = true

    def makeTransition(entity: RowEntity, toState: EntityState, transitionValidator: (EntityState, EntityState) => Boolean = defaultValidator)(implicit ec: ExecutionContext): DBIO[Int] = {
      val toTimestamp = Instant.now()
      val selectCurrentState = (for {
        s <- states.sortBy(_.timestamp.desc)
        e <- s.entity if e.id === entity.id
      } yield s.id).take(1).result.headOption
      // TODO: validate
      def insertTransition(from: Long): DBIO[Int] = {
        val transition = RowTransition(entity.id, from, toState, toTimestamp)
        transitions += transition
      }
      val insertState = states += RowState(None, entity.id, toState, toTimestamp)
      def deleteTransition(from: Long): DBIO[Int] = transitions.filter(_.entityId === entity.id).filter(_.from === from).delete
      (selectCurrentState flatMap { x =>
        val from = x.get
        insertTransition(from) andThen insertState andThen deleteTransition(from)
      }).transactionally
    }

  }

  _schemas += entities.schema

}
