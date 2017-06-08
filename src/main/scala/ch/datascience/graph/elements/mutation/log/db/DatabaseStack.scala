package ch.datascience.graph.elements.mutation.log.db

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

/**
  * Created by johann on 07/06/17.
  */
class DatabaseStack(protected val dbConfig: DatabaseConfig[JdbcProfile])
  extends RequestLogComponent
    with ResponseLogComponent
    with JdbcProfileComponent
    with ImplicitsComponent
