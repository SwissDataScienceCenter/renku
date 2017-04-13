package ch.datascience.typesystem.orchestration

import ch.datascience.typesystem.model.table.DatabaseStack
import slick.jdbc.JdbcBackend.Database

/**
  * Created by johann on 04/04/17.
  */
trait DatabaseComponent {

  val db: Database

  val dal: DatabaseStack

  def close(): Unit = {
    db.close()
  }

}
