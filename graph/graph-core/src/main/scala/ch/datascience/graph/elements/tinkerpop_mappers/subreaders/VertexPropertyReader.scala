package ch.datascience.graph.elements.tinkerpop_mappers.subreaders

import ch.datascience.graph.elements.persisted.{Path, PersistedVertexProperty, VertexPropertyPath}
import ch.datascience.graph.elements.tinkerpop_mappers.extracted.ExtractedVertexProperty
import ch.datascience.graph.elements.tinkerpop_mappers.{KeyReader, Reader, ValueReader, VertexPropertyIdReader}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 30/05/17.
  */
case class VertexPropertyReader(valueReader: ValueReader)
  extends Reader[(Path, ExtractedVertexProperty), PersistedVertexProperty]
    with RecordReaderHelper {

  def read(t: (Path, ExtractedVertexProperty))(implicit ec: ExecutionContext): Future[PersistedVertexProperty] = {
    val (parent, prop) = t

    for {
      id <- VertexPropertyIdReader.read(prop.id)
      key <- KeyReader.read(prop.key)
      value <-valueReader.read((key, prop.value))
      path = VertexPropertyPath(parent, id)
      properties = userPropertiesFilter(prop.properties)
      extractedProperties <- Future.traverse(properties){ prop => LeafPropertyReader(valueReader).read((path, prop)) }
      extractedPropertiesAsMap = (for (prop <- extractedProperties) yield prop.key -> prop).toMap
    } yield PersistedVertexProperty(id, parent, key, value, extractedPropertiesAsMap)
  }

}
