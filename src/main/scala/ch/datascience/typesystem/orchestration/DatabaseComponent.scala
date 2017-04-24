package ch.datascience.typesystem.orchestration

import ch.datascience.typesystem.external.DatabaseConfigComponent
import ch.datascience.typesystem.relationaldb.DatabaseStack
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile

/**
  * Created by johann on 04/04/17.
  */
trait DatabaseComponent extends DatabaseConfigComponent[JdbcProfile] {

  protected def dal: DatabaseStack

  def close(): Unit = {
    db.close()
  }

}
