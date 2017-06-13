package ch.datascience.graph.elements.mutation.log.db

//import ch.datascience.graph.elements.mutation.log.DatabaseConfigComponent
import play.api.db.slick.HasDatabaseConfig
import slick.jdbc.JdbcProfile

/**
  * Created by johann on 07/06/17.
  */
trait JdbcProfileComponent extends HasDatabaseConfig[JdbcProfile]
