package ch.datascience.graph.elements.mappers.tinkerpop

import ch.datascience.graph.elements.persistence.NewVertex
import ch.datascience.graph.elements.{Property, RichProperty}
import ch.datascience.graph.values.BoxedValue
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.{GraphTraversal, GraphTraversalSource}
import org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality
import org.apache.tinkerpop.gremlin.structure.{Vertex => GraphVertex}

/**
  * Created by johann on 24/05/17.
  */
class CreateVertexOperationWriter[
TypeId,
Key : StringWriter,
MetaProp <: Property[Key, BoxedValue],
Prop <: RichProperty[Key, BoxedValue, BoxedValue, MetaProp]
] extends Writer[NewVertex[TypeId, Key, BoxedValue, BoxedValue, MetaProp, Prop], (GraphTraversalSource) => GraphTraversal[GraphVertex, GraphVertex]] {

  def write(x: NewVertex[TypeId, Key, BoxedValue, BoxedValue, MetaProp, Prop]): (GraphTraversalSource) => GraphTraversal[GraphVertex, GraphVertex] = {
    def apply(t: GraphTraversalSource): GraphTraversal[GraphVertex, GraphVertex] = {
      var traversal: GraphTraversal[GraphVertex, GraphVertex] = t.addV()
      for ((key, multiProperty) <- x.properties) {
        val k = implicitly[StringWriter[Key]].write(key)
        val cardinality = multiProperty.cardinality
        val tinkerpopCardinality = Cardinality.valueOf(cardinality.name.toLowerCase)

        for (property <- multiProperty) {
          val v = BoxedWriter.write(property.value)

          val metaKeyValues: Seq[java.lang.Object] = (for {
            (metaKey, metaProp) <- property.properties
            mk = implicitly[StringWriter[Key]].write(metaKey)
            mv = BoxedWriter.write(metaProp.value)
            obj <- Seq(mk, mv)
          } yield obj).toSeq

          traversal = traversal.property(tinkerpopCardinality, k, v, metaKeyValues: _*)
        }
      }

      traversal
    }

    apply _
  }

}
