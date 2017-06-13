package ch.datascience.graph.elements.mutation.worker

import ch.datascience.graph.elements.mutation.create.{CreateEdgeOperation, CreateVertexOperation}
import ch.datascience.graph.elements.mutation.{Mutation, Operation}
import ch.datascience.graph.elements.mutation.json.MutationFormat
import ch.datascience.graph.elements.mutation.log.dao.RequestDAO
import ch.datascience.graph.elements.mutation.log.model.Event
import ch.datascience.graph.elements.mutation.tinkerpop_mappers.{CreateEdgeOperationMapper, CreateVertexOperationMapper}
import ch.datascience.graph.elements.new_.NewEdge
import ch.datascience.graph.elements.persisted.{PersistedEdge, PersistedVertex}
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.janusgraph.core.JanusGraph
import org.janusgraph.graphdb.relations.RelationIdentifier
import play.api.libs.json._

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 07/06/17.
  */
class ResponseWorker(
  protected val queue: Queue[JsValue],
  protected val graph: JanusGraph,
  protected val ec: ExecutionContext
) extends GraphComponent {

  start()

  def start(): Unit = {
    Future.successful(()).map{ _ => this.work() }
  }

  def work(): Unit = {
    while (queue.nonEmpty) {
      val event = queue.dequeue
      try {
        processOneEvent(event)
      } catch {
        case e: Throwable =>
          println(s"Unhandled exception: $e")
          e.printStackTrace()
      }
    }
    queue.register().future.map{ _ => this.work() }
  }

  def processOneEvent(event: JsValue): Unit = {
    val mutation = (event \ "query").as[Mutation](MutationFormat)
    val operations = mutation.operations

    // tempId -> persistedId map
    val idMap: mutable.Map[NewEdge#NewVertexType#TempId, NewEdge#PersistedVertexType#Id] = mutable.Map.empty

    val g = graph.traversal()

    execute {
      val results = for {
        op <- operations
      } yield processOperation(g, op, idMap)

      val json = JsObject(Seq("results" -> JsArray(results)))

      println(s"TODO: push to response db: $json")
    }
  }

  def processOperation(g: GraphTraversalSource, op: Operation, idMap: mutable.Map[NewEdge#NewVertexType#TempId, NewEdge#PersistedVertexType#Id]): JsValue = op match {
    case o: CreateVertexOperation =>
      val vertex = CreateVertexOperationMapper(o)(g).next()
      val id = vertex.id().asInstanceOf[PersistedVertex#Id]
      idMap += o.vertex.tempId -> id
      JsObject(Seq("id" -> JsNumber(id)))
    case o: CreateEdgeOperation =>
      val edge = CreateEdgeOperationMapper(idMap.toMap)(o)(g).next()
      val id = edge.id().asInstanceOf[RelationIdentifier].toString
      JsObject(Seq("id" -> JsString(id)))
    case o => throw new IllegalArgumentException(s"Unsupported operation: $o")
  }

  private[this] implicit lazy val e: ExecutionContext = ec

}
