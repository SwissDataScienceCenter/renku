package ch.datascience.graph.elements.mutation.worker

import java.util.UUID

import ch.datascience.graph.elements.mutation.create.{CreateEdgeOperation, CreateVertexOperation, CreateVertexPropertyOperation}
import ch.datascience.graph.elements.mutation.json.MutationFormat
import ch.datascience.graph.elements.mutation.log.dao.ResponseDAO
import ch.datascience.graph.elements.mutation.tinkerpop_mappers.{CreateEdgeOperationMapper, CreateVertexOperationMapper, CreateVertexPropertyOperationMapper, UpdateVertexPropertyOperationMapper}
import ch.datascience.graph.elements.mutation.update.UpdateVertexPropertyOperation
import ch.datascience.graph.elements.mutation.{Mutation, Operation}
import ch.datascience.graph.elements.new_.NewEdge
import ch.datascience.graph.elements.persisted.PersistedVertex
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.janusgraph.core.JanusGraph
import org.janusgraph.graphdb.relations.RelationIdentifier
import play.api.libs.json._

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by johann on 07/06/17.
  */
class ResponseWorker(
  protected val queue: Queue[(UUID, JsValue)],
  protected val graph: JanusGraph,
  protected val dao: ResponseDAO,
  protected val ec: ExecutionContext
) extends GraphComponent {

  start()

  def start(): Unit = {
    Future.successful(()).map{ _ => this.work() }
  }

  def work(): Unit = {
    while (queue.nonEmpty) {
      val (requestId, event) = queue.dequeue
      try {
        processOneEvent(requestId, event)
      }
      catch {
        case e: Throwable =>
          println(s"Unhandled exception: $e")
          e.printStackTrace()
      }
    }
    queue.register().future.map{ _ => this.work() }
  }

  def processOneEvent(requestId:UUID, event: JsValue): Unit = {
    val mutation = (event \ "query").as[Mutation](MutationFormat)
    val operations = mutation.operations

    // tempId -> persistedId map
    val idMap: mutable.Map[NewEdge#NewVertexType#TempId, NewEdge#PersistedVertexType#Id] = mutable.Map.empty

    val g = graph.traversal()

    try {
      execute {
        val results = for {
          op <- operations
        } yield processOperation(g, op, idMap)

        val json = JsObject(Seq("status" -> JsString("success"), "results" -> JsArray(results)))

        //      println(s"TODO: push to response db: $json")
        val logResponse = dao.add(requestId, json)
        Await.result(logResponse, Duration.Inf)
      }
    }
    catch {
      case e: Throwable =>
        println(s"Request failed")
        dao.add(requestId, JsObject(Seq("status" -> JsString("failed"), "reason" -> JsString(e.getMessage))))
        throw e
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
    case o: CreateVertexPropertyOperation =>
      val vertex = CreateVertexPropertyOperationMapper(o)(g).next()
      val id = vertex.id().asInstanceOf[PersistedVertex#Id]
      JsObject(Seq("id" -> JsNumber(id)))
    case o: UpdateVertexPropertyOperation =>
      val vertex = UpdateVertexPropertyOperationMapper(o)(g).next()
      val id = vertex.id().asInstanceOf[PersistedVertex#Id]
      JsObject(Seq("id" -> JsNumber(id)))
    case o => throw new IllegalArgumentException(s"Unsupported operation: $o")
  }

  private[this] implicit lazy val e: ExecutionContext = ec

}
