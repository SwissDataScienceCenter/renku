package ch.datascience.graph.types

import ch.datascience.graph.naming.json.{NamespaceAndNameReads, NamespaceAndNameWrites}
import play.api.libs.json.{Format, Reads, Writes}

/**
  * Created by johann on 23/05/17.
  */
package object json {

  implicit lazy val propKeyReads: Reads[PropertyKey#Key] = NamespaceAndNameReads
  implicit lazy val propKeyWrites: Writes[PropertyKey#Key] = NamespaceAndNameWrites

  implicit lazy val propertyKeyReads: Reads[PropertyKey] = new PropertyKeyReads
  implicit lazy val propertyKeyWrites: Writes[PropertyKey] = new PropertyKeyWrites

  implicit lazy val DataTypeFormat: Format[DataType] = Format(DataTypeReads, DataTypeWrites)

  implicit lazy val CardinalityFormat: Format[Cardinality] = Format(CardinalityReads, CardinalityWrites)

  implicit lazy val MultiplicityFormat: Format[Multiplicity] = Format(MultiplicityReads, MultiplicityWrites)

}
