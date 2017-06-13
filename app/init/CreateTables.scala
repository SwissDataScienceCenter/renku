package init

//import ch.datascience.graph.types.persistence.DatabaseConfigComponent
import ch.datascience.graph.types.persistence.relationaldb.DatabaseStack
import play.api.db.slick.HasDatabaseConfig
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

/**
  * Created by johann on 13/04/17.
  */
object CreateTables extends HasDatabaseConfig[JdbcProfile] {

  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("slick.dbs.sqldb")

  def createTables(): Future[Unit] = {

    import profile.api._

    val dal = new DatabaseStack(dbConfig)

    val createSchemas: DBIO[Unit] = dal.schemas.map(_.asInstanceOf[profile.SchemaDescription]).reduce((x, y) => x ++ y).create

    db.run(createSchemas)
  }

}
