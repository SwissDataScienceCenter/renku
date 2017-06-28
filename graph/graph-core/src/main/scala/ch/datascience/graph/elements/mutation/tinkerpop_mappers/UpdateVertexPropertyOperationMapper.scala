package ch.datascience.graph.elements.mutation.tinkerpop_mappers

import ch.datascience.graph.elements.detached.DetachedProperty
import ch.datascience.graph.elements.mutation.update.UpdateVertexPropertyOperation
import ch.datascience.graph.elements.new_.NewRichProperty
import ch.datascience.graph.elements.tinkerpop_mappers.{KeyWriter, ValueWriter}
import org.apache.tinkerpop.gremlin.process.traversal.Traverser
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.{GraphTraversal, GraphTraversalSource}
import org.apache.tinkerpop.gremlin.structure.{Vertex, VertexProperty}

/**
  * Created by johann on 20/06/17.
  */
object UpdateVertexPropertyOperationMapper extends Mapper {

  final type OperationType = UpdateVertexPropertyOperation

  final type Source = Vertex
  final type Element = Vertex

  def apply(op: UpdateVertexPropertyOperation): (GraphTraversalSource) => GraphTraversal[Vertex, Vertex] = {
    val vertexProperty = op.vertexProperty
    val newVertexProperty = NewRichProperty(vertexProperty.parent, vertexProperty.key, op.newValue, vertexProperty.properties.mapValues{ p =>
      DetachedProperty(p.key, p.value)
    })

    { s: GraphTraversalSource =>
      // Get parent
      val t1 = PathHelper.follow(s, vertexProperty.path).asInstanceOf[GraphTraversal[Vertex, VertexProperty[java.lang.Object]]]

      // Check validity of update
      val t2 = t1.map(updateFilter(ValueWriter.write(vertexProperty.value)))

      t2.drop().iterate()

      val card = s.getGraph.features().vertex().getCardinality(KeyWriter.write(vertexProperty.key))
      val t4 = PathHelper.follow(s, vertexProperty.parent).asInstanceOf[GraphTraversal[Vertex, Vertex]]
      val t5 = addProperty(t4, card, newVertexProperty)

      t5
    }
  }

  private[this] type VertexPropertyType = OperationType#ElementType

  private[this] def updateFilter(oldValue: java.lang.Object): java.util.function.Function[Traverser[VertexProperty[java.lang.Object]], VertexProperty[java.lang.Object]] = new java.util.function.Function[Traverser[VertexProperty[java.lang.Object]], VertexProperty[java.lang.Object]] {
    def apply(t: Traverser[VertexProperty[java.lang.Object]]): VertexProperty[java.lang.Object] = {
      val vertexProperty = t.get()
      val storedValue = vertexProperty.value()
      if (storedValue != oldValue)
        throw new IllegalArgumentException(s"Invalid updated: expected old value: $oldValue, but got $storedValue")
      vertexProperty
    }
  }

  private[this] def parent: java.util.function.Function[Traverser[VertexProperty[java.lang.Object]], Vertex] = new java.util.function.Function[Traverser[VertexProperty[java.lang.Object]], Vertex] {
    def apply(t: Traverser[VertexProperty[java.lang.Object]]): Vertex = {
      t.get().element()
    }
  }

  private[this] def addProperty(t: GraphTraversal[Vertex, Vertex], card: VertexProperty.Cardinality, prop: NewRichProperty): GraphTraversal[Vertex, Vertex] = {
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
