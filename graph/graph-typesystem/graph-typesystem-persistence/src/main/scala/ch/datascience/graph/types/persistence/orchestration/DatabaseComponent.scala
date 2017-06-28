package ch.datascience.graph.types.persistence.orchestration

//import ch.datascience.graph.types.persistence.DatabaseConfigComponent
import ch.datascience.graph.types.persistence.relationaldb.DatabaseStack
import play.api.db.slick.HasDatabaseConfig
import slick.jdbc.JdbcProfile

/**
  * Created by johann on 04/04/17.
  */
//trait DatabaseComponent extends DatabaseConfigComponent[JdbcProfile] {
trait DatabaseComponent extends HasDatabaseConfig[JdbcProfile] {

//  protected def dal: DatabaseStack
  protected val dal: DatabaseStack

  def close(): Unit = {
    db.close()
  }

}
