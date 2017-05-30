package ch.datascience.graph.elements.tinkerpop_mappers.extracted

/**
  * Created by johann on 30/05/17.
  */
case class ExtractedVertexProperty(
  id: Any,
  key: String,
  value: Any,
  properties: Seq[ExtractedLeafProperty]
) extends ExtractedProperty
