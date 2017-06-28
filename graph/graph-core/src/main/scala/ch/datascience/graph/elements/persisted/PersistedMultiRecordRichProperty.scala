package ch.datascience.graph.elements.persisted

import ch.datascience.graph.elements.RichProperty

/**
  * Created by johann on 29/05/17.
  */
trait PersistedMultiRecordRichProperty
  extends PersistedMultiRecordProperty
    with RichProperty {

  final type Prop = PersistedRecordProperty

}
