package ch.datascience.graph.elements.persisted

/**
  * Created by johann on 11/05/17.
  */
sealed abstract class Path

//final case class VertexPath[+VertexId](vertexId: VertexId) extends Path
final case class VertexPath(vertexId: PersistedVertex#Id) extends Path

//final case class EdgePath[+EdgeId](edgeId: EdgeId) extends Path
final case class EdgePath(edgeId: PersistedEdge#Id) extends Path

sealed abstract class PropertyPath extends Path {
  def parent: Path
}

//case class PropertyPathFromRecord[+Key](parent: Path, key: Key) extends PropertyPath
final case class PropertyPathFromRecord(parent: Path, key: PersistedProperty#Key) extends PropertyPath

sealed abstract class PropertyPathFromMultiRecord[+PropertyId] extends PropertyPath {
  def propertyId: PropertyId
}

final case class VertexPropertyPath(parent: Path, propertyId: PersistedVertexProperty#Id)
  extends PropertyPathFromMultiRecord[PersistedVertexProperty#Id]
