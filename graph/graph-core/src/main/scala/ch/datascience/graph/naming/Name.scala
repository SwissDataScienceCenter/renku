package ch.datascience.graph.naming

import scala.util.matching.Regex
import scala.util.{Success, Try}

/**
  * Created by jmt on 15/05/17.
  */
object Name {

  lazy val namePattern: Regex = raw"([-A-Za-z0-9_/.]+)".r

  def apply(name: String): String = {
    require(nameIsValid(name), s"Invalid name: '$name' (Pattern: $namePattern)")
    name
  }

  def unapply(name: String): Option[String] = Try( apply(name) ) match {
    case Success(ns) => Some(ns)
    case _           => None
  }

  def nameIsValid(name: String): Boolean = name match {
    case namePattern(_) => true
    case _              => false
  }

}

