package ch.datascience.graph.elements.tinkerpop_mappers.extractors

import ch.datascience.graph.elements.tinkerpop_mappers.extracted.ExtractedLeafProperty
import org.apache.tinkerpop.gremlin.structure.Property

/**
  * Created by johann on 30/05/17.
  */
object LeafPropertyExtractor extends Extractor[Property[java.lang.Object], ExtractedLeafProperty] {

  def apply(prop: Property[java.lang.Object]): ExtractedLeafProperty = {
    ExtractedLeafProperty(prop.key(), prop.value())
  }

}
