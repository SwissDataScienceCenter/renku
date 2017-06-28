package ch.datascience.graph.types.persistence.relationaldb

//import ch.datascience.graph.types.persistence.DatabaseConfigComponent
import play.api.db.slick.HasDatabaseConfig
import slick.jdbc.JdbcProfile

/**
  * Created by johann on 17/03/17.
  */
//trait JdbcProfileComponent extends DatabaseConfigComponent[JdbcProfile]
trait JdbcProfileComponent extends HasDatabaseConfig[JdbcProfile]
