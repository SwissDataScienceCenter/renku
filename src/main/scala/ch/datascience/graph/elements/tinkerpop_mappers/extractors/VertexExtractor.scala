package ch.datascience.graph.elements.tinkerpop_mappers.extractors

import ch.datascience.graph.elements.tinkerpop_mappers.extracted.ExtractedVertex
import org.apache.tinkerpop.gremlin.structure.Vertex

import scala.collection.JavaConverters._

/**
  * Created by johann on 30/05/17.
  */
object VertexExtractor extends Extractor[Vertex, ExtractedVertex] {

  def apply(vertex: Vertex): ExtractedVertex = {
    val properties = vertex.properties[java.lang.Object]().asScala.toList
    val extractedProperties = for {
      prop <- properties
    } yield VertexPropertyExtractor(prop)
    ExtractedVertex(vertex.id(), vertex.label(), extractedProperties)
  }

}
