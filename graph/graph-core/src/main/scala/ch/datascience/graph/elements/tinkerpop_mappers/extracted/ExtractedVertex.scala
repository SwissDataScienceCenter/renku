package ch.datascience.graph.elements.tinkerpop_mappers.extracted

/**
  * Created by johann on 30/05/17.
  */
case class ExtractedVertex(
  id: Any,
  label: String,
  properties: Seq[ExtractedVertexProperty]
)
