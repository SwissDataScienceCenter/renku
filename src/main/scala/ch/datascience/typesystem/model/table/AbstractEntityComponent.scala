package ch.datascience.typesystem.model.table

import java.time.Instant
import java.util.UUID

import ch.datascience.typesystem.model.EntityState
import ch.datascience.typesystem.model.row.{AbstractEntity, Entity, State}
import slick.jdbc.JdbcProfile
import slick.lifted.{CompiledFunction, ForeignKeyQuery}

/**
  * Created by johann on 20/03/17.
  */
trait AbstractEntityComponent { this: JdbcProfileComponent with EntityComponent with StateComponent =>

  import profile.api._

  trait AbstractEntityTable[A <: AbstractEntity] extends Table[A] {

    // Columns
    def id: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)

    // Foreign keys
    def entity: ForeignKeyQuery[Entities, Entity] =
      foreignKey(s"${this.tableName}_FK_ENTITIES", id, entities)(_.id)

  }

  trait AbstractEntitiesTableQuery[A <: AbstractEntity, TA <: Table[A] with AbstractEntityTable[A]] { this: TableQuery[TA] =>

    val findById: CompiledFunction[Rep[UUID] => Query[TA, A, Seq], Rep[UUID], UUID, Query[TA, A, Seq], Seq[A]] =
      Compiled { (entityId: Rep[UUID]) => this.filter(_.id === entityId) }

    def add(entity: A): DBIO[Int] = {
      val insertEntity = entities += Entity(entity.id, entity.entityType)
      val insertState = states += State(None, entity.id, EntityState.PENDING, Instant.now())
      val insertConcrete = this += entity
      (insertEntity andThen insertState andThen insertConcrete).transactionally
    }

  }

}
