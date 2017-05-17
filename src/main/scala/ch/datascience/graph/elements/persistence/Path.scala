package ch.datascience.graph.elements.persistence

/**
  * Created by johann on 11/05/17.
  */
sealed abstract class Path

final case class VertexPath[+VertexId](vertexId: VertexId) extends Path

final case class EdgePath[+VertexId, +EdgeId](fromVertex: VertexId, edgeId: EdgeId) extends Path

sealed abstract class PropertyPath extends Path {
  def parent: Path
}

case class PropertyPathFromRecord[+Key](parent: Path, key: Key) extends PropertyPath

case class PropertyPathFromMultiRecord[+PropertyId](
  parent: Path,
  propertyId: PropertyId
) extends PropertyPath
