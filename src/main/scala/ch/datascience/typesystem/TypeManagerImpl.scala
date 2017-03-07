package ch.datascience.typesystem
import java.util.Optional

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.JanusGraph

import scala.util.{Failure, Success, Try}

/**
  * Created by johann on 07/03/17.
  */
case class TypeManagerImpl(graph: JanusGraph) extends TypeManager {

  override def findDomain(namespace: String): Option[GraphDomain] = ???

  override def createDomain(domain: GraphDomain): Try[GraphDomain] = ???

  override def findAuthority(name: String): Option[Authority] = {
    val g: GraphTraversalSource = graph.traversal()
    val optionalVertex: Optional[Vertex] = g.V().has(Constants.PropertyKeys.typeKey, Constants.Types.authorityType)
      .has(Constants.PropertyKeys.authorityNameKey, name).tryNext()
    g.close()
    optionalVertex.isPresent match {
      case true =>
        val vertex: Vertex = optionalVertex.get()
        val authorityName: String = vertex.property[String](Constants.PropertyKeys.authorityNameKey).value()
        val authority: Authority = Authority.builder().name(authorityName).make()
        Some(authority)
      case false => None
    }
  }

  override def createAuthority(authority: Authority): Try[Authority] = findAuthority(authority.name).isDefined match {
    case true => Failure(new IllegalArgumentException(s"authority ${authority.name} already exists"))
    case false =>
      val tx = graph.tx()
      val g: GraphTraversalSource = graph.traversal()

      val res: Try[Unit] = try {
        Success(g.addV().property(Constants.PropertyKeys.typeKey, Constants.Types.authorityType).property(Constants.PropertyKeys.authorityNameKey, authority.name).iterate())
      } catch {
        case e: Exception => Failure(e)
      } finally {
        g.close()
        tx.commit()
      }

      res.flatMap { _ =>
        findAuthority(authority.name) match {
          case Some(a) => Success(a)
          case None => Failure(new RuntimeException("???"))
        }
      }
  }

}
