package ch.datascience.typesystem.orchestration

import java.util.UUID

import ch.datascience.typesystem.model.DataType
import ch.datascience.typesystem.model.row.{GraphDomain, PropertyKey}
import org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by johann on 04/04/17.
  */
trait PropertyKeyOrchestrator { this: DatabaseComponent with GraphComponent =>

  import dal.profile.api._

  def createPropertyKey(graphDomain: GraphDomain,
                        name: String,
                        dataType: DataType = DataType.STRING,
                        cardinality: Cardinality = Cardinality.single)(implicit ec: ExecutionContext): Future[PropertyKey] = {
    val fullname = s"${graphDomain.namespace}:$name"
    val propertyKeyId = UUID.randomUUID()
    val propertyKey = PropertyKey(propertyKeyId, graphDomain.id, name, dataType, cardinality)
    val insertPropertyKey = dal.propertyKeys add propertyKey
    val propagateChange = insertPropertyKey map { _ =>
      val future = gal.run(gal.addPropertyKey(fullname, dataType, cardinality)).map(_ => propertyKey)
      Await.result(future, Duration.Inf)
    }
    db.run(propagateChange.transactionally)
  }

}
