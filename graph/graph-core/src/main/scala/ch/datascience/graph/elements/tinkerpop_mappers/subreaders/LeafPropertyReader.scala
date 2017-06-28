package ch.datascience.graph.elements.tinkerpop_mappers.subreaders

import ch.datascience.graph.elements.persisted.{Path, PersistedRecordProperty}
import ch.datascience.graph.elements.tinkerpop_mappers.extracted.ExtractedLeafProperty
import ch.datascience.graph.elements.tinkerpop_mappers.{KeyReader, Reader, ValueReader}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 30/05/17.
  */
case class LeafPropertyReader(valueReader: ValueReader) extends Reader[(Path, ExtractedLeafProperty), PersistedRecordProperty] {

  def read(t: (Path, ExtractedLeafProperty))(implicit ec: ExecutionContext): Future[PersistedRecordProperty] = {
    val (parent, prop) = t

    for {
      key <- KeyReader.read(prop.key)
      value <- valueReader.read((key, prop.value))
    } yield PersistedRecordProperty(parent, key, value)
  }

}
