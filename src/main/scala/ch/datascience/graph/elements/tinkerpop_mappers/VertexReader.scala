package ch.datascience.graph.elements.tinkerpop_mappers

import ch.datascience.graph.elements.persisted.PersistedVertex
import ch.datascience.graph.elements.tinkerpop_mappers.extractors.VertexExtractor
import ch.datascience.graph.elements.tinkerpop_mappers.subreaders.{VertexReader => VertexSubReader}
import ch.datascience.graph.scope.PropertyScope
import org.apache.tinkerpop.gremlin.structure.Vertex

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 30/05/17.
  */
case class VertexReader(scope: PropertyScope) extends Reader[Vertex, PersistedVertex] {

  override def read(vertex: Vertex)(implicit ec: ExecutionContext): Future[PersistedVertex] = {
    val extractedVertex = VertexExtractor(vertex)
    reader.read(extractedVertex)
  }

  private[this] lazy val reader: VertexSubReader = VertexSubReader(scope)

}
