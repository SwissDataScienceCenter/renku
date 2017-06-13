package ch.datascience.graph.elements.mutation.log.dao

//import ch.datascience.graph.elements.mutation.log.DatabaseConfigComponent
import ch.datascience.graph.elements.mutation.log.db.DatabaseStack
import play.api.db.slick.HasDatabaseConfig
import slick.jdbc.JdbcProfile

/**
  * Created by johann on 07/06/17.
  */
trait DatabaseComponent extends HasDatabaseConfig[JdbcProfile] {

  protected val dal: DatabaseStack

  def close(): Unit = {
    db.close()
  }

}
