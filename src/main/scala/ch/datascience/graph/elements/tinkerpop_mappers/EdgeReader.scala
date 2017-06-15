package ch.datascience.graph.elements.tinkerpop_mappers

import ch.datascience.graph.elements.persisted.PersistedEdge
import ch.datascience.graph.elements.tinkerpop_mappers.extractors.{EdgeExtractor, VertexExtractor}
import ch.datascience.graph.elements.tinkerpop_mappers.subreaders.{EdgeReader => EdgeSubReader}
import ch.datascience.graph.scope.PropertyScope
import org.apache.tinkerpop.gremlin.structure.Edge

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 30/05/17.
  */
case class EdgeReader(scope: PropertyScope) extends Reader[Edge, PersistedEdge] {

  override def read(edge: Edge)(implicit ec: ExecutionContext): Future[PersistedEdge] = {
    val extractedEdge = EdgeExtractor(edge)
    reader.read(extractedEdge)
  }

  private[this] lazy val reader: EdgeSubReader = EdgeSubReader(scope)

}
