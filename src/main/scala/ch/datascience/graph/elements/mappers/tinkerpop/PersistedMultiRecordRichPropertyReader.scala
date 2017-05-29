//package ch.datascience.graph.elements.mappers.tinkerpop
//
//import ch.datascience.graph.elements.persisted.{Path, PropertyPathFromMultiRecord}
//import ch.datascience.graph.elements.persisted.impl.ImplPersistedMultiRecordRichProperty
//import ch.datascience.graph.elements.persistence.{PersistedMultiRecordRichProperty, PersistedRecordProperty, PropertyPathFromMultiRecord}
//import ch.datascience.graph.values.BoxedValue
//import org.apache.tinkerpop.gremlin.structure.{VertexProperty => GraphMultiRecordRichProperty}
//
//import scala.collection.JavaConverters.asScalaIteratorConverter
//import scala.concurrent.{ExecutionContext, Future}
//
//class PersistedMultiRecordRichPropertyReader[Id, Key : StringReader](val parent: Path)(implicit ir: Reader[java.lang.Object, Id], kvr: KeyValueReader[Key, BoxedValue])
//  extends Reader[GraphMultiRecordRichProperty[java.lang.Object], PersistedMultiRecordRichProperty[Id, Key, BoxedValue, BoxedValue, PersistedRecordProperty[Key, BoxedValue]]] {
//
//  def read(property: GraphMultiRecordRichProperty[java.lang.Object])(implicit ec: ExecutionContext): Future[PersistedMultiRecordRichProperty[Id, Key, BoxedValue, BoxedValue, PersistedRecordProperty[Key, BoxedValue]]] = {
//    for {
//      id <- ir.read(property.id())
//      key <- implicitly[StringReader[Key]].read(property.key())
//      value <- kvr.read(key -> property.value())
//      path = PropertyPathFromMultiRecord(parent, id)
//      metaPropertyReader = new PersistedRecordPropertyReader[Key](path)
//      metaProps <- Future.traverse(property.properties[java.lang.Object]().asScala)(metaPropertyReader.read)
//    } yield {
//      val metaProperties = (for {
//        metaProperty <- metaProps
//      } yield metaProperty.key -> metaProperty).toMap
//      ImplPersistedMultiRecordRichProperty(parent, id, key, value, metaProperties)
//    }
//  }
//
//}
//
/////**
////  * Created by johann on 22/05/17.
////  */
////class PersistedMultiRecordRichPropertyReader[Id, Key : StringReader](val parent: Path)(implicit ir: Reader[java.lang.Object, Id], kvr: KeyValueReader[Key, BoxedValue])
////  extends Reader[GraphMultiRecordRichProperty[java.lang.Object], PersistedMultiRecordRichProperty[Id, Key, BoxedValue, BoxedValue, PersistedRecordProperty[Key, BoxedValue]]] {
////
////  def read(property: GraphMultiRecordRichProperty[java.lang.Object])(implicit ec: ExecutionContext): Future[PersistedMultiRecordRichProperty[Id, Key, BoxedValue, BoxedValue, PersistedRecordProperty[Key, BoxedValue]]] = {
////    for {
////      id <- ir.read(property.id())
////      key <- implicitly[StringReader[Key]].read(property.key())
////      value <- kvr.read(key -> property.value())
////      path = PropertyPathFromMultiRecord(parent, id)
////      metaPropertyReader = new PersistedRecordPropertyReader[Key](path)
////      metaProps <- Future.traverse(property.properties[java.lang.Object]().asScala)(metaPropertyReader.read)
////    } yield {
////      val metaProperties = (for {
////        metaProperty <- metaProps
////      } yield metaProperty.key -> metaProperty).toMap
////      ImplPersistedMultiRecordRichProperty(parent, id, key, value, metaProperties)
////    }
////  }
////
////}
