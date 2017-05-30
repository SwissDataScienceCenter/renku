package ch.datascience.graph.elements.tinkerpop_mappers.subreaders

import ch.datascience.graph.elements.tinkerpop_mappers.KeyReader
import ch.datascience.graph.elements.tinkerpop_mappers.extracted.ExtractedProperty

import scala.util.Try

/**
  * Created by johann on 30/05/17.
  */
trait RecordReaderHelper {

  protected[this] def userPropertiesFilter[A <: ExtractedProperty](properties: Seq[A]): Seq[A] = {
    for {
      prop <- properties
      keyTry = Try { KeyReader.readSync(prop.key) }
      if keyTry.isSuccess
    } yield prop
  }

}
