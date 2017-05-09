package ch.datascience.graph.types.persistence.relationaldb

import java.time.Instant
import java.util.UUID

import ch.datascience.graph.types.persistence.model.{AbstractEntity, EntityState}
import ch.datascience.graph.types.persistence.model.relational.{RowAbstractEntity, RowEntity, RowState}
import slick.lifted.{CompiledFunction, ForeignKeyQuery}

/**
  * Created by johann on 20/03/17.
  */
trait AbstractEntityComponent { this: JdbcProfileComponent with EntityComponent with StateComponent =>

  import profile.api._

  trait AbstractEntityTable[A <: RowAbstractEntity] extends Table[A] {

    // Columns
    def id: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)

    // Foreign keys
    def entity: ForeignKeyQuery[Entities, RowEntity] = foreignKey(s"${this.tableName}_FK_ENTITIES", id, entities)(_.id)

  }

  trait AbstractEntitiesTableQuery[A <: RowAbstractEntity, MA <: AbstractEntity[A], TA <: Table[A] with AbstractEntityTable[A]] { this: TableQuery[TA] =>

    lazy val findRowById: CompiledFunction[Rep[UUID] => Query[TA, A, Seq], Rep[UUID], UUID, Query[TA, A, Seq], Seq[A]] =
      Compiled { (entityId: Rep[UUID]) => this.filter(_.id === entityId) }

    //def findById(id: UUID): Query[TA, MA, Seq]

    def add(entity: MA): DBIO[Int] = addRow(entity.toRow)

    def addRow(rowEntity: A): DBIO[Int] = {
      val insertEntity = entities += RowEntity(rowEntity.id, rowEntity.entityType)
      val insertState = states += RowState(None, rowEntity.id, EntityState.Pending, Instant.now())
      val insertConcrete = this += rowEntity
      (insertEntity andThen insertState andThen insertConcrete).transactionally
    }

  }

}
