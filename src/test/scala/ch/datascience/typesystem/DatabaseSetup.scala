package ch.datascience.typesystem

import ch.datascience.typesystem.external.DatabaseConfigComponent
import ch.datascience.typesystem.relationaldb.DatabaseStack
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by johann on 13/04/17.
  */
trait DatabaseSetup extends BeforeAndAfterAll with DatabaseConfigComponent[JdbcProfile] { this : Suite =>

  import profile.api._

  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("db-test")

//  val profile: JdbcProfile = dbConfig.profile
//
//  import profile.api._
//  val db: Database = Database.forConfig("", config = dbConfig.config)
//
//  class DatabaseStackTest(val profile: JdbcProfile) extends DatabaseStack
//
//  val dal = new DatabaseStackTest(profile)

  val dal = new DatabaseStack(dbConfig)

  override protected def beforeAll(): Unit = {
    println("CreateDB")
    val createSchemas: DBIO[Unit] = dal.schemas.map(_.asInstanceOf[profile.SchemaDescription]).reduce((x, y) => x ++ y).create
    val run = db.run(createSchemas)
    Await.result(run, Duration.Inf)

    super.beforeAll()
  }

  override protected def afterAll(): Unit = {
    try super.afterAll()
    finally {
      println("DeleteDB")
      val deleteSchemas: DBIO[Unit] = dal.schemas.map(_.asInstanceOf[profile.SchemaDescription]).reduce((x,y) => x ++ y).drop
      val run = db.run(deleteSchemas)
      Await.result(run, Duration.Inf)
    }
  }

}
