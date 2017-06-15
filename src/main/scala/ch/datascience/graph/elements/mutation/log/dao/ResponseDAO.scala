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
class ResponseDAO(
  protected val ec: ExecutionContext,
  protected val dbConfig: DatabaseConfig[JdbcProfile],
  protected val dal: DatabaseStack
) extends DatabaseComponent {

  import profile.api._

  def all(): Future[Seq[Event]] = db.run( dal.responses.result )

  def findById(id: UUID): Future[Option[Event]] = {
    db.run( dal.responses.findById(id).result.headOption )
  }

  def add(requestId: UUID, event: JsValue): Future[Event] = {
    db.run( dal.responses.add(requestId, event) )
  }

//  def add(event: JsValue): Future[Event] = {
//    db.run( dal.responses.add(event)(ec) )
//  }

}
