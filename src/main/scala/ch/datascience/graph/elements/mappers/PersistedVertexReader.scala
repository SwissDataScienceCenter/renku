package ch.datascience.graph.elements.mappers

import ch.datascience.graph.elements.{BoxedValue, ListValue, SetValue, SingleValue}
import ch.datascience.graph.elements.persistence.VertexPath
import ch.datascience.graph.elements.persistence.impl.ImplPersistedVertex
import ch.datascience.graph.scope.PropertyScope
import ch.datascience.graph.types.Cardinality
import org.apache.tinkerpop.gremlin.structure.{Vertex => GraphVertex}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 19/05/17.
  */
class PersistedVertexReader[Id, TypeId, Key : StringReader, MetaKey : StringReader, PropId](scope: PropertyScope[Key])(implicit ir: Reader[java.lang.Object, Id], pir: Reader[java.lang.Object, PropId], kvr: KeyValueReader[Key, BoxedValue], kvr2: KeyValueReader[MetaKey, BoxedValue])
  extends Reader[GraphVertex, ImplPersistedVertex[Id, TypeId, Key, BoxedValue, MetaKey, BoxedValue, PropId]] {

  def read(vertex: GraphVertex)(implicit ec: ExecutionContext): Future[ImplPersistedVertex[Id, TypeId, Key, BoxedValue, MetaKey, BoxedValue, PropId]] = {
    for {
      id <- ir.read( vertex.id() )
      path = VertexPath(id)
      propertyReader = new PersistedMultiRecordRichPropertyReader[PropId, Key, MetaKey](path)
      props <- Future.traverse(vertex.properties[java.lang.Object]().asScala.toIterable)(propertyReader.read)
      propsByKey = props.groupBy(_.key)
      definitions <- scope.getPropertiesFor(propsByKey.keySet)
    } yield {
      val properties = for {
        (key, props) <- propsByKey
        cardinality = definitions(key).cardinality
      } yield key -> {
        cardinality match {
          case Cardinality.Single => SingleValue(props.head)
          case Cardinality.Set => SetValue(props.toList)
          case Cardinality.List => ListValue(props.toList)
        }
      }
      ImplPersistedVertex(id, Set(), properties)
    }
  }

}
