package ch.datascience.graph.elements.tinkerpop_mappers.subreaders

import ch.datascience.graph.elements.persisted._
import ch.datascience.graph.elements.tinkerpop_mappers._
import ch.datascience.graph.elements.tinkerpop_mappers.extracted.ExtractedEdge
import ch.datascience.graph.scope.PropertyScope

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 30/05/17.
  */
case class EdgeReader(scope: PropertyScope) extends Reader[ExtractedEdge, PersistedEdge] with RecordReaderHelper {

  def read(edge: ExtractedEdge)(implicit ec: ExecutionContext): Future[PersistedEdge] = {
    for {
      id <- EdgeIdReader.read(edge.id)
      label <- EdgeLabelReader.read(edge.label)
      from <- VertexIdReader.read(edge.from)
      to <- VertexIdReader.read(edge.to)
      path = EdgePath(id)
      properties = userPropertiesFilter(edge.properties)
      extractedProperties <- Future.traverse(properties){ prop => LeafPropertyReader(valueReader).read((path, prop)) }
      propsByKey = (for { prop <- extractedProperties } yield prop.key -> prop).toMap
    } yield {
      PersistedEdge(id, label, from, to, propsByKey)
    }
  }

  private[this] lazy val valueReader: ValueReader = ValueReader(scope)

}
