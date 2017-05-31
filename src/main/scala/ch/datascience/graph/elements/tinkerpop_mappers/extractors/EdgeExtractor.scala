package ch.datascience.graph.elements.tinkerpop_mappers.extractors

import ch.datascience.graph.elements.tinkerpop_mappers.extracted.ExtractedEdge
import org.apache.tinkerpop.gremlin.structure.Edge

import scala.collection.JavaConverters._

/**
  * Created by johann on 30/05/17.
  */
object EdgeExtractor extends Extractor[Edge, ExtractedEdge] {

  def apply(edge: Edge): ExtractedEdge = {
    val properties = edge.properties[java.lang.Object]().asScala.toList
    val extractedProperties = for {
      prop <- properties
    } yield LeafPropertyExtractor(prop)
    // outVertex ---label---> inVertex
    val from = edge.outVertex().id()
    val to = edge.inVertex().id()
    ExtractedEdge(edge.id(), edge.label(), from, to, extractedProperties)
  }

}
