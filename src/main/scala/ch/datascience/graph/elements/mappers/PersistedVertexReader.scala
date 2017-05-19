package ch.datascience.graph.elements.mappers

import ch.datascience.graph.elements.BoxedValue
import ch.datascience.graph.elements.builders.PersistedVertexBuilder
import ch.datascience.graph.elements.persistence.impl.ImplPersistedVertex
import ch.datascience.graph.scope.{NamedTypeScope, PropertyScope}
import org.apache.tinkerpop.gremlin.structure.{Vertex => GraphVertex}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 19/05/17.
  */
class PersistedVertexReader[Id, TypeId, Key : StringReader, MetaKey : StringReader, PropId](scope: PropertyScope[Key], metaScope: PropertyScope[MetaKey])(implicit ir: Reader[java.lang.Object, Id], kvr: KeyValueReader[Key, BoxedValue], kvr2: KeyValueReader[MetaKey, BoxedValue])
  extends Reader[GraphVertex, ImplPersistedVertex[Id, TypeId, Key, BoxedValue, MetaKey, BoxedValue, PropId]] {

  def read(vertex: GraphVertex)(implicit ec: ExecutionContext): Future[ImplPersistedVertex[Id, TypeId, Key, BoxedValue, MetaKey, BoxedValue, PropId]] = {
    val builder = new PersistedVertexBuilder[Id, TypeId, Key, BoxedValue, MetaKey, BoxedValue, PropId]()
    for {
      id <- ir.read( vertex.id() )
      //TODO: Read properties
      //card <- scope.getPropertiesFor(  )
    } yield {
      builder.id = id
      // ???
      builder.result()
    }
  }

}
