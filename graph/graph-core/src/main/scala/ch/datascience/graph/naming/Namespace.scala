package ch.datascience.graph.naming

import scala.util.matching.Regex
import scala.util.{Success, Try}

/**
  * Created by jmt on 15/05/17.
  */
object Namespace {

  lazy val namespacePattern: Regex = raw"([-A-Za-z0-9_/.]*)".r

  def apply(namespace: String): String = {
    require(namespaceIsValid(namespace), s"Invalid namespace: '$namespace' (Pattern: $namespacePattern)")
    namespace
  }

  def unapply(namespace: String): Option[String] = Try( apply(namespace) ) match {
    case Success(ns) => Some(ns)
    case _           => None
  }

  def namespaceIsValid(namespace: String): Boolean = namespace match {
    case namespacePattern(_) => true
    case _                   => false
  }

}
