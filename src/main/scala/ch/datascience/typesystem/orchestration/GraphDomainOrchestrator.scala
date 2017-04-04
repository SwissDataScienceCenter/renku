package ch.datascience.typesystem.orchestration

import java.util.UUID

import ch.datascience.typesystem.model.row.GraphDomain

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 04/04/17.
  */
trait GraphDomainOrchestrator { this: DatabaseComponent =>

  def createGraphDomain(namespace: String)(implicit ec: ExecutionContext): Future[GraphDomain] = {
    val graphDomainId = UUID.randomUUID()
    val graphDomain = GraphDomain(graphDomainId, namespace)
    val insertDomain = dal.graphDomains add graphDomain
    db.run(insertDomain) map { _ => graphDomain }
  }

}
