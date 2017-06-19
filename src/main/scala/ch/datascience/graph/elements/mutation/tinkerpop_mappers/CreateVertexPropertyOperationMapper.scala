package ch.datascience.graph.elements.mutation.tinkerpop_mappers

import ch.datascience.graph.Constants
import ch.datascience.graph.elements.mutation.create.{CreateVertexOperation, CreateVertexPropertyOperation}
import ch.datascience.graph.elements.tinkerpop_mappers.{CardinalityWriter, KeyWriter, TypeIdWriter, ValueWriter}
import ch.datascience.graph.types.Cardinality
import org.apache.tinkerpop.gremlin.process.traversal.Traverser
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.{GraphTraversal, GraphTraversalSource}
import org.apache.tinkerpop.gremlin.structure.{Vertex, VertexProperty}

/**
  * Created by johann on 30/05/17.
  */
case object CreateVertexPropertyOperationMapper extends Mapper {

  final type OperationType = CreateVertexPropertyOperation

  final type Source = Vertex
  final type Element = Vertex

  def apply(op: CreateVertexPropertyOperation): (GraphTraversalSource) => GraphTraversal[Vertex, Vertex] = {
    val vertexProperty = op.vertexProperty
    val parent = vertexProperty.parent

    { s: GraphTraversalSource =>
      // Get parent
      val t1 = PathHelper.follow(s, parent).asInstanceOf[GraphTraversal[Vertex, Vertex]]

      val card = s.getGraph.features().vertex().getCardinality(KeyWriter.write(vertexProperty.key))
      card match {
        case VertexProperty.Cardinality.single =>
          val t2 = t1.map(singleFilter(KeyWriter.write(vertexProperty.key)))
          addProperty(t2, card, vertexProperty)
        case VertexProperty.Cardinality.set =>
          val t2 = t1.map(setFilter(KeyWriter.write(vertexProperty.key), ValueWriter.write(vertexProperty.value)))
          addProperty(t2, card, vertexProperty)
        case VertexProperty.Cardinality.list => addProperty(t1, card, vertexProperty)
      }
    }
  }

  private[this] type VertexPropertyType = OperationType#ElementType

  private[this] def singleFilter(key: String): java.util.function.Function[Traverser[Vertex], Vertex] = new java.util.function.Function[Traverser[Vertex], Vertex] {
    def apply(t: Traverser[Vertex]): Vertex = {
      import scala.collection.JavaConverters._
      val vertex = t.get()
      if (vertex.properties(key).asScala.nonEmpty)
        throw new IllegalArgumentException(s"Property already exists: $key")
      vertex
    }
  }

  private[this] def setFilter(key: String, value: java.lang.Object): java.util.function.Function[Traverser[Vertex], Vertex] = new java.util.function.Function[Traverser[Vertex], Vertex] {
    def apply(t: Traverser[Vertex]): Vertex = {
      import scala.collection.JavaConverters._
      val vertex = t.get()
      if (vertex.properties[java.lang.Object](key).asScala.exists(_.value() == value))
        throw new IllegalArgumentException(s"Property already exists: $key, $value")
      vertex
    }
  }

  private[this] def addProperty(t: GraphTraversal[Vertex, Vertex], card: VertexProperty.Cardinality, prop: VertexPropertyType): GraphTraversal[Vertex, Vertex] = {
    val key = KeyWriter.write(prop.key)
    val value = ValueWriter.write(prop.value)
    val keyValues: Seq[java.lang.Object] = (for {
      metaProp <- prop.properties.values
      metaKey = KeyWriter.write(metaProp.key)
      metaValue = ValueWriter.write(metaProp.value)
      x <- Seq(metaKey, metaValue)
    } yield x).toSeq

    t.property(card, key, value, keyValues: _*)
  }

}
