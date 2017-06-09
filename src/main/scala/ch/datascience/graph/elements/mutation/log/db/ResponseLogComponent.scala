package ch.datascience.graph.elements.mutation.log.db

import java.time.Instant
import java.util.UUID

import ch.datascience.graph.elements.mutation.log.model.Event
import play.api.libs.json.JsValue
import slick.lifted.{CompiledFunction, ForeignKeyQuery, ProvenShape}

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 07/06/17.
  */
trait ResponseLogComponent { this: JdbcProfileComponent with ImplicitsComponent with RequestLogComponent =>

  import profile.api._

  class Responses(tag: Tag) extends Table[Event](tag, "RESPONSE_LOG") {

    def id: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)

    def event: Rep[JsValue] = column[JsValue]("EVENT")

    def created: Rep[Instant] = column[Instant]("CREATED")

    def * : ProvenShape[Event] = (id, event, created) <> (Event.tupled, Event.unapply)

    // Foreign keys
    def request: ForeignKeyQuery[Requests, Event] = foreignKey(s"RESPONSE_FK_REQUEST", id, requests)(_.id)

  }

  object responses extends TableQuery(new Responses(_)) {

    lazy val findById: CompiledFunction[Rep[UUID] => Query[Responses, Event, Seq], Rep[UUID], UUID, Query[Responses, Event, Seq], Seq[Event]] = {
      this.findBy(_.id)
    }

    def add(event: JsValue)(implicit ec: ExecutionContext): DBIO[Event] = insert(event).flatMap { id =>
      this.filter(_.id === id).result.head
    }

    private[this] def insert(event: JsValue): DBIO[UUID] = {
      val query = (for { event <- this } yield event.event) returning this.map(_.id)

      query += event
    }

  }

}
