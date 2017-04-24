package ch.datascience.typesystem.orchestration

import java.util.UUID

import ch.datascience.typesystem.relationaldb.row.GraphDomain

import scala.concurrent.Future

/**
  * Created by johann on 04/04/17.
  */
trait GraphDomainComponent { this: DatabaseComponent with ExecutionComponent =>

  import profile.api._

  object graphDomains {

    def all(): Future[Seq[GraphDomain]] = db.run(dal.graphDomains.result)

    def findByNamespace(namespace: String): Future[Option[GraphDomain]] = {
      db.run(dal.graphDomains.findByNamespace(namespace).result.headOption)
    }

    def createGraphDomain(namespace: String): Future[GraphDomain] = {
      val graphDomainId = UUID.randomUUID()
      val graphDomain = GraphDomain(graphDomainId, namespace)
      val insertDomain = dal.graphDomains add graphDomain
      db.run(insertDomain) map { _ => graphDomain }
    }

  }

}
