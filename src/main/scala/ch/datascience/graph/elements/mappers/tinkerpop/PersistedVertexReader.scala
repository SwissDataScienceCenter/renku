//package ch.datascience.graph.elements.mappers.tinkerpop
//
//import ch.datascience.graph.elements.persisted.VertexPath
//import ch.datascience.graph.elements.persisted.impl.ImplPersistedVertex
//import ch.datascience.graph.elements.persistence.{PersistedMultiRecordRichProperty, PersistedRecordProperty, PersistedVertex}
//import ch.datascience.graph.elements.{ListValue, SetValue, SingleValue}
//import ch.datascience.graph.scope.PropertyScope
//import ch.datascience.graph.types.Cardinality
//import ch.datascience.graph.values.BoxedValue
//import org.apache.tinkerpop.gremlin.structure.{Vertex => GraphVertex}
//
//import scala.collection.JavaConverters._
//import scala.concurrent.{ExecutionContext, Future}
//
///**
//  * Created by johann on 19/05/17.
//  */
//class PersistedVertexReader[Id, TypeId, Key : StringReader, PropId](scope: PropertyScope[Key])(implicit ir: Reader[java.lang.Object, Id], pir: Reader[java.lang.Object, PropId], kvr: KeyValueReader[Key, BoxedValue])
//  extends Reader[GraphVertex, PersistedVertex[Id, TypeId, Key, BoxedValue, BoxedValue, PersistedRecordProperty[Key, BoxedValue], PropId, PersistedMultiRecordRichProperty[PropId, Key, BoxedValue, BoxedValue, PersistedRecordProperty[Key, BoxedValue]]]] {
//
//  def read(vertex: GraphVertex)(implicit ec: ExecutionContext): Future[PersistedVertex[Id, TypeId, Key, BoxedValue, BoxedValue, PersistedRecordProperty[Key, BoxedValue], PropId, PersistedMultiRecordRichProperty[PropId, Key, BoxedValue, BoxedValue, PersistedRecordProperty[Key, BoxedValue]]]] = {
//    //TODO: Read types
//
//    type Prop = PersistedMultiRecordRichProperty[PropId, Key, BoxedValue, BoxedValue, PersistedRecordProperty[Key, BoxedValue]]
//    for {
//      id <- ir.read( vertex.id() )
//      path = VertexPath(id)
//      propertyReader = new PersistedMultiRecordRichPropertyReader[PropId, Key](path)
//      props <- Future.traverse(vertex.properties[java.lang.Object]().asScala.toIterable)(propertyReader.read)
//      propsByKey = props.groupBy(_.key)
//      definitions <- scope.getPropertiesFor(propsByKey.keySet)
//    } yield {
//      val properties = for {
//        (key, props) <- propsByKey
//        cardinality = definitions(key).cardinality
//      } yield key -> {
//        cardinality match {
//          case Cardinality.Single => SingleValue[Key, BoxedValue, Prop](props.head)
//          case Cardinality.Set => SetValue[Key, BoxedValue, Prop](props.toList)
//          case Cardinality.List => ListValue[Key, BoxedValue, Prop](props.toList)
//        }
//      }
//      ImplPersistedVertex(id, Set.empty[TypeId], properties)
//    }
//  }
//
//}
