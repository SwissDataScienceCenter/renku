package ch.datascience.graph.elements

import ch.datascience.graph.Constants
import ch.datascience.graph.naming.json.{NamespaceAndNameFormat, StringFormat}
import ch.datascience.graph.values.json.BoxedValueFormat
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath}

/**
  * Created by johann on 30/05/17.
  */
package object json {

  lazy val KeyFormat: StringFormat[Constants.Key] = NamespaceAndNameFormat
  lazy val TypeIdFormat: StringFormat[Constants.TypeId] = NamespaceAndNameFormat
  lazy val EdgeLabelFormat: StringFormat[Constants.EdgeLabel] = NamespaceAndNameFormat

  lazy val PropertyFormat: Format[Property] = (
      (JsPath \ "key").format[Property#Key](KeyFormat) and
        JsPath.format[Property#Value](BoxedValueFormat)
    )(
      { (k, v) =>
        new Property {
          def key: Key = k
          def value: Value = v
        }
      },
      unlift(Property.unapply)
    )

}
