package ch.datascience.graph.types.persistence.relationaldb

import java.time.Instant
import java.util.UUID

import ch.datascience.graph.types.persistence.model._
import slick.lifted.{CompiledFunction, ForeignKeyQuery, MappedProjection}

import scala.concurrent.Future

/**
  * Created by johann on 20/03/17.
  */
trait AbstractEntityComponent { this: JdbcProfileComponent with EntityComponent with StateComponent =>

  import profile.api._

  trait AbstractEntityTable[A <: AbstractEntity] extends Table[A] {

    // Columns
    def id: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)

    // Foreign keys
    def entity: ForeignKeyQuery[Entities, Entity] = foreignKey(s"${this.tableName}_FK_ENTITIES", id, entities)(_.id)

  }

  trait AbstractEntitiesTableQuery[A <: AbstractEntity, RA <: RichAbstractEntity[A], TA <: Table[A] with AbstractEntityTable[A]] { this: TableQuery[TA] =>

    def add(entity: RA): DBIO[Unit] = {
      DBIO.seq(preInsertEntity(entity), insertEntity(entity), postInsertEntity(entity)).transactionally
    }

    protected def insertEntity(entity: RA): DBIO[Unit] = {
      DBIO.seq(this += entity.unlifted)
    }

    protected def preInsertEntity(entity: RA): DBIO[Unit] = {
      val insertEntity = entities += Entity(entity.id, entity.entityType)
      val insertState = states += State(None, entity.id, EntityState.Pending, Instant.now())
      DBIO.seq(insertEntity, insertState)
    }

    protected def postInsertEntity(entity: RA): DBIO[Unit] = {
      // TODO: Commit by transitioning state
      DBIO.from(Future.successful(()))
    }

  }

}
