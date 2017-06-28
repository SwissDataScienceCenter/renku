package ch.datascience.graph.values

import play.api.libs.json.Format

/**
  * Created by johann on 24/05/17.
  */
package object json {

  implicit val BoxedValueFormat: Format[BoxedValue] = Format(BoxedValueReads, BoxedValueWrites)

}
