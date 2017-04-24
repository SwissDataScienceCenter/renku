package ch.datascience.typesystem.model

import ch.datascience.typesystem.{Cardinality, DataType, PropertyDefinition => PropertyKeyBase}

import scala.util.matching.Regex

/**
  * Created by johann on 24/04/17.
  */
case class PropertyKey(namespace: String,
                       name: String,
                       dataType: DataType = DataType.String,
                       cardinality: Cardinality = Cardinality.Single)
  extends PropertyKeyBase[String] {

  override lazy val key: String = s"$namespace:$name"

}

//object PropertyKey {
//
//  lazy val fqdnRegex: Regex = "([^:]*):(.*)".r
//
//  def apply(fqdn: String, dataType: DataType, cardinality: Cardinality): PropertyKey = fqdn match {
//    case fqdnRegex(namespace, name) => PropertyKey(namespace, name, dataType, cardinality)
//  }
//
//}
