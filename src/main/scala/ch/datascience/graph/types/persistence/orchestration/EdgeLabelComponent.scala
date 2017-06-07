package ch.datascience.graph.types.persistence.orchestration

import java.util.UUID

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.types.Multiplicity
import ch.datascience.graph.types.persistence.model.{GraphDomain, RichEdgeLabel}

import scala.concurrent.Future

/**
  * Created by johann on 07/06/17.
  */
trait EdgeLabelComponent {
  this: ExecutionComponent with DatabaseComponent with GraphComponent =>

  import profile.api._
  import dal._

  object edgeLabels {

    def all(): Future[Seq[RichEdgeLabel]] = {
      db.run(dal.edgeLabels.mapped.result)
    }

    def findById(id: UUID): Future[Option[RichEdgeLabel]] = {
      db.run(dal.edgeLabels.findById(id).result.headOption)
    }

    def findByNamespaceAndName(key: NamespaceAndName): Future[Option[RichEdgeLabel]] = {
      findByNamespaceAndName(key.namespace, key.name)
    }

    def findByNamespaceAndName(namespace: String, name: String): Future[Option[RichEdgeLabel]] = {
      db.run(dal.edgeLabels.findByNamespaceAndName(namespace, name).result.headOption)
    }

    def createEdgeLabel(
                         graphDomain: GraphDomain,
                         name: String,
                         multiplicity: Multiplicity = Multiplicity.Simple
                       ): Future[RichEdgeLabel] = {
      val fullname = s"${graphDomain.namespace}:$name"
      val edgeLabel = RichEdgeLabel(UUID.randomUUID(), graphDomain, name, multiplicity)
      val insertEdgeLabel = dal.edgeLabels add edgeLabel
      val propagateChange = insertEdgeLabel flatMap { _ =>
        val future = gdb.run(gal.edgeLabels.addEdgeLabel(fullname, multiplicity)).map(_ => edgeLabel)
        DBIO.from(future)
      }
      db.run(propagateChange.transactionally)
    }

    def createEdgeLabel(
                         namespace: String,
                         name: String,
                         multiplicity: Multiplicity
                       ): Future[RichEdgeLabel] = {
      val selectGraphDomain = db.run(dal.graphDomains.findByNamespace(namespace).result.headOption.map(_.get))
      selectGraphDomain flatMap { graphDomain => createEdgeLabel(graphDomain, name, multiplicity) }
    }

  }

}
