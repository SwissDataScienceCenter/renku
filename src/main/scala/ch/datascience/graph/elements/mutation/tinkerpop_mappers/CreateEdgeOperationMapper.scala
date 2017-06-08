package ch.datascience.graph.elements.mutation.tinkerpop_mappers

import ch.datascience.graph.Constants
import ch.datascience.graph.elements.mutation.create.CreateEdgeOperation
import ch.datascience.graph.elements.new_.NewEdge
import ch.datascience.graph.elements.tinkerpop_mappers.{EdgeLabelWriter, KeyWriter, TypeIdWriter, ValueWriter}
import org.apache.tinkerpop.gremlin.process.traversal.Traversal
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.{GraphTraversal, GraphTraversalSource}
import org.apache.tinkerpop.gremlin.structure.{Edge, Vertex}

/**
  * Created by johann on 07/06/17.
  */
case class CreateEdgeOperationMapper(idMap: Map[NewEdge#NewVertexType#TempId, NewEdge#PersistedVertexType#Id]) extends Mapper {

  type OperationType = CreateEdgeOperation

  type Source = Vertex
  type Element = Edge

  def apply(op: CreateEdgeOperation): (GraphTraversalSource) => Traversal[Vertex, Edge] = {
    val edge = op.edge
    val label = EdgeLabelWriter.write(edge.label)
    val from = getPersistedVertexId(edge.from)
    val to = getPersistedVertexId(edge.to)

    { s:GraphTraversalSource =>
      // Add Edge
      val t1 = s.V(to).as("to").V(from).addE(label).to("to")

      // Add types
      val t2 = edge.types.foldLeft(t1) { (t, typeId) => t.addType(typeId) }

      // Add properties
      val t3 = edge.properties.values.foldLeft(t2) { (t, property) =>
          t.addProperty(property)
      }

      // We don't iterate here
      t3
    }

  }

  private[this] type TempId = NewEdge#NewVertexType#TempId
  private[this] type PersistedId = NewEdge#PersistedVertexType#Id

  private[this] def getPersistedVertexId(ref: NewEdge#VertexReference): java.lang.Object = ref match {
    case Left(tempId) => Long.box(idMap(tempId))
    case Right(id) => Long.box(id)
  }

  private[this] implicit class RichEdgeTraversal(t: GraphTraversal[Vertex, Edge]) {

    def addType(typeId: NewEdge#TypeId): GraphTraversal[Vertex, Edge] = {
      val value = TypeIdWriter.write(typeId)
      val props = Seq.empty
      t.property(Constants.TypeKey, value)
    }

    def addProperty(prop: NewEdge#Prop): GraphTraversal[Vertex, Edge] = {
      val key = KeyWriter.write(prop.key)
      val value = ValueWriter.write(prop.value)

      t.property(key, value)
    }

  }

}
