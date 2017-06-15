package ch.datascience.graph.elements.mutation.log.dao

import java.util.UUID

import ch.datascience.graph.elements.mutation.log.db.DatabaseStack
import ch.datascience.graph.elements.mutation.log.model.Event
import play.api.libs.json.JsValue
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 07/06/17.
  */
class RequestDAO(
  protected val ec: ExecutionContext,
  protected val dbConfig: DatabaseConfig[JdbcProfile],
  protected val dal: DatabaseStack
) extends DatabaseComponent {

  import profile.api._

  def all(): Future[Seq[Event]] = db.run( dal.requests.result )

  def findByIdWithResponse(id: UUID): Future[Option[(Event, Option[Event])]] = {
    db.run( dal.requests.findByIdWithResponse(id).result.headOption )
  }

  def add(event: JsValue): Future[Event] = {
    db.run( dal.requests.add(event)(ec) )
  }

}
