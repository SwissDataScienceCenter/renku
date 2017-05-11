package ch.datascience.graph.elements.persistence

/**
  * Created by johann on 11/05/17.
  */
sealed abstract class Path

case class VertexPath[+VertexId](vertexId: VertexId) extends Path

// TODO: case class EdgePath

trait RecordPath[+Key] extends Path

trait MultiRecordPath[+PropertyId] extends Path

sealed abstract class PropertyPath extends Path {
  def parent: Path
}

case class PropertyPathWithKey[+Key](parent: RecordPath[Key], key: Key) extends PropertyPath

case class PropertyPathWithId[+PropertyId](
  parent: MultiRecordPath[PropertyId],
  propertyId: PropertyId
) extends PropertyPath
