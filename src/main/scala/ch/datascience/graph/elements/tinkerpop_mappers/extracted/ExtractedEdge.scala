package ch.datascience.graph.elements.tinkerpop_mappers.extracted

/**
  * Created by johann on 30/05/17.
  */
case class ExtractedEdge(
  id: Any,
  label: String,
  from: Any,
  to: Any,
  properties: Seq[ExtractedLeafProperty]
)
