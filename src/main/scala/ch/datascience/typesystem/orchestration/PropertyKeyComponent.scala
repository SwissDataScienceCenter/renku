package ch.datascience.typesystem.orchestration

import java.util.UUID

import ch.datascience.typesystem.relationaldb.row.{GraphDomain, PropertyKey}
import ch.datascience.typesystem.{Cardinality, DataType}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by johann on 04/04/17.
  */
trait PropertyKeyComponent { this: ExecutionComponent with DatabaseComponent with GraphComponent =>

  import profile.api._

  object propertyKeys {

    def all(): Future[Seq[PropertyKey]] = db.run(dal.propertyKeys.result)

    def findById(id: UUID): Future[Option[PropertyKey]] = {
      db.run(dal.propertyKeys.findById(id).result.headOption)
    }

    def findByNamespaceAndName(namespace: String, name: String): Future[Option[PropertyKey]] = {
      db.run(dal.propertyKeys.findByNamespaceAndName(namespace, name).result.headOption)
    }

    def createPropertyKey(graphDomain: GraphDomain,
                          name: String,
                          dataType: DataType = DataType.String,
                          cardinality: Cardinality = Cardinality.Single): Future[PropertyKey] = {
      val fullname = s"${graphDomain.namespace}:$name"
      val propertyKeyId = UUID.randomUUID()
      val propertyKey = PropertyKey(propertyKeyId, graphDomain.id, name, dataType, cardinality)
      val insertPropertyKey = dal.propertyKeys add propertyKey
      val propagateChange = insertPropertyKey map { _ =>
        val future = gdb.run(gal.propertyKeys.addPropertyKey(fullname, dataType, cardinality)).map(_ => propertyKey)
        Await.result(future, Duration.Inf)
      }
      db.run(propagateChange.transactionally)
    }

    def createPropertyKey(namespace: String,
                          name: String,
                          dataType: DataType,
                          cardinality: Cardinality): Future[PropertyKey] = {
      val selectGraphDomain = db.run(dal.graphDomains.findByNamespace(namespace).result.headOption.map(_.get))
      selectGraphDomain.flatMap(graphDomain => createPropertyKey(graphDomain, name, dataType, cardinality))
    }

  }

}
