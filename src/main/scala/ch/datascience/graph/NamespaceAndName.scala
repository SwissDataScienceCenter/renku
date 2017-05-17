package ch.datascience.graph

import scala.util.matching.Regex

/**
  * Created by johann on 30/04/17.
  */
case class NamespaceAndName(namespace: String, name: String) {
  // Apply namespace and name requirements
  Namespace(namespace)
  Name(name)

  def asString: String = s"$namespace:$name"

}

object NamespaceAndName {

//  lazy val namespacePattern: Regex = raw"([-A-Za-z0-9_/]*)".r
//  lazy val namePattern     : Regex = raw"([-A-Za-z0-9_/]+)".r
  lazy val separatePattern : Regex = s"([^:]*):(.*)".r

  def apply(namespaceAndName: String): NamespaceAndName = namespaceAndName match {
    case separatePattern(namespace, name) => NamespaceAndName(namespace, name)
    case _                                => throw new IllegalArgumentException(s"Cannot find separator ':'")
  }

//  def namespaceIsValid(namespace: String): Boolean = namespace match {
//    case namespacePattern(_) => true
//    case _                   => false
//  }
//
//  def nameIsValid(name: String): Boolean = name match {
//    case namePattern(_) => true
//    case _              => false
//  }

}
