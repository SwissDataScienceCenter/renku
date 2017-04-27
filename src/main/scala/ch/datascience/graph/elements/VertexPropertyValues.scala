package ch.datascience.graph.elements

/**
  * Created by johann on 27/04/17.
  */
sealed abstract class VertexPropertyValues[Key, Value : ValidValue, MetaKey] extends Element with Iterable[VertexProperty[Key, Value, MetaKey]] {

  // Allows easy iteration
  final def asSeq: Seq[VertexProperty[Key, Value, MetaKey]] = this match {
    case SingleValue(vp) => List(vp)
    case SetValue(map) => map.values.toSeq
    case ListValue(vps) => vps
  }

  override final def iterator: Iterator[VertexProperty[Key, Value, MetaKey]] = asSeq.toIterator

}

final case class SingleValue[Key, Value : ValidValue, MetaKey](vertexProperty: VertexProperty[Key, Value, MetaKey]) extends VertexPropertyValues[Key, Value, MetaKey]

final case class SetValue[Key, Value : ValidValue, MetaKey](vertexProperties: Map[Value, VertexProperty[Key, Value, MetaKey]]) extends VertexPropertyValues[Key, Value, MetaKey]

final case class ListValue[Key, Value : ValidValue, MetaKey](vertexProperties: Seq[VertexProperty[Key, Value, MetaKey]]) extends VertexPropertyValues[Key, Value, MetaKey]
