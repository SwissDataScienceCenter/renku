package ch.datascience.graph.elements.tinkerpop_mappers

import ch.datascience.graph.elements.persisted.{PersistedVertexProperty, VertexPath}
import ch.datascience.graph.elements.tinkerpop_mappers.extractors.VertexPropertyExtractor
import ch.datascience.graph.elements.tinkerpop_mappers.subreaders.{VertexPropertyReader => VertexPropertySubReader}
import ch.datascience.graph.scope.PropertyScope
import org.apache.tinkerpop.gremlin.structure.VertexProperty

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 30/05/17.
  */
case class VertexPropertyReader(scope: PropertyScope) extends Reader[VertexProperty[java.lang.Object], PersistedVertexProperty] {

  def read(prop: VertexProperty[Object])(implicit ec: ExecutionContext): Future[PersistedVertexProperty] = {
    val extractedProperty = VertexPropertyExtractor(prop)
    val parent = prop.element()

    for {
      id <- VertexIdReader.read(parent.id())
      parentPath = VertexPath(id)
      mappedVertex <- reader.read((parentPath, extractedProperty))
    } yield mappedVertex
  }

  private[this] lazy val valueReader: ValueReader = ValueReader(scope)

  private[this] lazy val reader: VertexPropertySubReader = VertexPropertySubReader(valueReader)

}
