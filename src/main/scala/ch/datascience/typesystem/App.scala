package ch.datascience.typesystem
import java.sql.DriverManager
import java.util.UUID

import ch.datascience.typesystem.model.DataType
import ch.datascience.typesystem.model.row.{GraphDomain, PropertyKey}
import ch.datascience.typesystem.model.table._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Created by johann on 15/03/17.
  */
object App {

  def main(args: Array[String]): Unit = {

    val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("h2mem1")

    java.lang.Class.forName(dbConfig.config.getString("db.driver"))

    import dbConfig.profile.api._

    val db = Database.forConfig("", config = dbConfig.config)

    try {

      val dal = new DataAccessLayer(dbConfig.profile)

      val schemas = dal.tables.map(_.schema).reduce((x, y) => x ++ y)
      val createSchemas: DBIO[Unit] = schemas.create

      println("Generated SQL for the DDL:")
      schemas.createStatements.foreach(println)

      val sql1 = db.run(createSchemas)
      Await.result(sql1, Duration.Inf)

      val insertDomainAndPropertyKeys: DBIO[Unit] = {
        val graphDomainId = UUID.randomUUID()
        val insertDomain = dal.graphDomains add GraphDomain(graphDomainId, "foo")
        val insertPropertyKeys = (dal.propertyKeys add PropertyKey(UUID.randomUUID(), graphDomainId, "str")) andThen
          (dal.propertyKeys add PropertyKey(UUID.randomUUID(), graphDomainId, "int", DataType.INTEGER))
        (insertDomain andThen insertPropertyKeys).map(_ => ())
      }

      val sql2 = db.run(insertDomainAndPropertyKeys.transactionally)
      Await.result(sql2, Duration.Inf)

      val insertMoreDomainAndPropertyKeys: DBIO[Int] = {
        val graphDomainId = UUID.randomUUID()
        val insertDomain = dal.graphDomains add GraphDomain(graphDomainId, "bar")
        val insertPropertyKeys = (dal.propertyKeys add PropertyKey(UUID.randomUUID(), graphDomainId, "abc")) andThen
          (dal.propertyKeys add PropertyKey(UUID.randomUUID(), graphDomainId, "xyz", DataType.DOUBLE))
        val selectGD = dal.graphDomains.findByNamespace("foo").result.headOption
        val badInsert = for {
          gd <- selectGD
          n <- gd.map(x => dal.propertyKeys add PropertyKey(UUID.randomUUID(), x.id, "int")).getOrElse(DBIO.successful(0))
        } yield n
        insertDomain andThen insertPropertyKeys andThen badInsert
      }

      val sql2_ = db.run(insertMoreDomainAndPropertyKeys.transactionally).map(println)
      Await.result(sql2_, Duration.Inf)

      Await.result(db.run(dal.entities.result.map(println)), Duration.Inf)
      Await.result(db.run(dal.graphDomains.result.map(println)), Duration.Inf)
      Await.result(db.run(dal.propertyKeys.result.map(println)), Duration.Inf)

      val selectPKs = for {
        pk <- dal.propertyKeys
        gd <- pk.graphDomain
        e <- pk.entity
      } yield (e.entityType, gd.namespace, pk.name, pk.dataType, pk.cardinality)
      val sql3 = db.run(selectPKs.result).map(println)
      println("Generated SQL for the join query:\n" + selectPKs.result.statements)
      Await.result(sql3, Duration.Inf)

      val sql4 = db.run(dal.propertyKeys.findByNamespaceAndName("bar", "int").result).map(println)
      Await.result(sql4, Duration.Inf)

    } finally {
      db.close()

      for (driver <- DriverManager.getDrivers.asScala) {
        DriverManager.deregisterDriver(driver)
      }
    }

//    val createSchemas: DBIO[Unit] = (graphDomains.schema ++ propertyKeys.schema).create
//
//    println("Generated SQL for the DDL:")
//    (graphDomains.schema ++ propertyKeys.schema).createStatements.foreach(println)
//
//    val insertDomain: DBIO[Long] =
//      (graphDomains returning graphDomains.map(_.id)) += GraphDomain(None, "foo")
//
//    val insertPropertyKeys: DBIO[Option[Int]] = insertDomain flatMap { graphDomainId =>
//      propertyKeys ++= Seq(
//        PropertyKey(None, graphDomainId, "str"),
//        PropertyKey(None, graphDomainId, "int", DataType.Integer)
//      )
//    }
//
//    val f = db.run(createSchemas andThen insertPropertyKeys)
//
//    Await.result(f, Duration.Inf)
//
//    println(f.value)
//
//    val selectPK: Query[(Rep[String], Rep[String]), (String, String), Seq] = for {
//      domain <- graphDomains if domain.namespace === "foo"
//      pk <- propertyKeys if pk.graphDomainId === domain.id && pk.name === "str"
//    } yield (domain.namespace, pk.name)
//
//    println("Generated SQL for the join query:\n" + selectPK.result.statements)
//
//    val f2 =  db.run(selectPK.result).map(res => res.map({case (x, y) => s"$x:$y"}).foreach(println))
//
//    Await.result(f2, Duration.Inf)
//
//    val x = propertyKeys.findBy(_.name)
//    val f3 = db.run(x("int").result).map(println)
//
//    Await.result(f3, Duration.Inf)
//
//    val f4 = db.run(graphDomains.findByNamespace("foob").result.headOption).map(println)
//
//    Await.result(f4, Duration.Inf)
//
//    println(db.source.getClass.getCanonicalName)
//
//    val badInserts: DBIO[Option[Int]] = (graphDomains ++= Seq(
//      GraphDomain(None, "bar"),
//      GraphDomain(None, "bar")
//    )).transactionally
//
//    try {
//      val f5 = db.run(badInserts andThen graphDomains.result).map(println)
//
//      Await.result(f5, Duration.Inf)
//    } catch {
//      case e: Exception => ()
//    }
//
//    Await.result(db.run(graphDomains.result).map(println), Duration.Inf)
//
//    db.close()

  }

}
