//package ch.datascience.graph.elements.mappers.tinkerpop
//
//import ch.datascience.graph.naming.NamespaceAndName
//
//import scala.concurrent.{ExecutionContext, Future}
//
///**
//  * Created by johann on 19/05/17.
//  */
//case object NamespaceAndNameReader extends StringReader[NamespaceAndName] {
//
//  def read(x: String)(implicit ec: ExecutionContext): Future[NamespaceAndName] = Future.successful( NamespaceAndName(x) )
//
//}
