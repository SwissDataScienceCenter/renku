package ch.datascience.graph.elements.persistence

/**
  * Created by johann on 11/05/17.
  */
sealed abstract class Path

final case class VertexPath[+VertexId](vertexId: VertexId) extends MultiRecordPath

// TODO: case class EdgePath

sealed trait RecordPath extends Path

sealed trait MultiRecordPath extends Path

sealed abstract class PropertyPath extends Path {
  def parent: Path
}

case class PropertyPathFromRecord[+Key](parent: RecordPath, key: Key) extends PropertyPath

case class PropertyPathFromMultiRecord[+PropertyId](
  parent: MultiRecordPath,
  propertyId: PropertyId
) extends PropertyPath

final class RichPropertyPathFromRecord[+Key](parent: RecordPath, key: Key)
  extends PropertyPathFromRecord(parent, key) with RecordPath

final class RichPropertyPathFromMultiRecord[+PropertyId](parent: MultiRecordPath, propertyId: PropertyId)
  extends PropertyPathFromMultiRecord(parent, propertyId) with RecordPath
