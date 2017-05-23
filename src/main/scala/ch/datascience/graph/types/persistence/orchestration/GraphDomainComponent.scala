package ch.datascience.graph.types.persistence.orchestration

import java.util.UUID

import ch.datascience.graph.types.persistence.model.GraphDomain

import scala.concurrent.Future

/**
  * Created by johann on 04/04/17.
  */
trait GraphDomainComponent { this: DatabaseComponent with ExecutionComponent =>

  import profile.api._
  import dal._

  object graphDomains {

    def all(): Future[Seq[GraphDomain]] = db.run( dal.graphDomains.result )

    def findById(id: UUID): Future[Option[GraphDomain]] = {
      db.run( dal.graphDomains.findById(id).result.headOption )
    }

    def findByNamespace(namespace: String): Future[Option[GraphDomain]] = {
      db.run( dal.graphDomains.findByNamespace(namespace).result.headOption )
    }

    def createGraphDomain(namespace: String): Future[GraphDomain] = {
      val graphDomainId = UUID.randomUUID()
      val graphDomain = GraphDomain(graphDomainId, namespace)
      val insertDomain = dal.graphDomains add graphDomain
      db.run(insertDomain) map { _ => graphDomain }
    }

  }

}
