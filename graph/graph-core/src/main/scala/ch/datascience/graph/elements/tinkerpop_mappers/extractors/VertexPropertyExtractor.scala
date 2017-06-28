package ch.datascience.graph.elements.tinkerpop_mappers.extractors

import ch.datascience.graph.elements.tinkerpop_mappers.extracted.ExtractedVertexProperty
import org.apache.tinkerpop.gremlin.structure.VertexProperty

import scala.collection.JavaConverters._

/**
  * Created by johann on 30/05/17.
  */
object VertexPropertyExtractor extends Extractor[VertexProperty[java.lang.Object], ExtractedVertexProperty] {

  def apply(prop: VertexProperty[java.lang.Object]): ExtractedVertexProperty = {
    val metaProperties = prop.properties[java.lang.Object]().asScala.toList
    val extractedMetaProperties = for {
      prop <- metaProperties
    } yield LeafPropertyExtractor(prop)
    ExtractedVertexProperty(prop.id(), prop.key(), prop.value(), extractedMetaProperties)
  }

}
