package ch.datascience.graph.elements.builders

import ch.datascience.graph.elements.BoxedOrValidValue
import ch.datascience.graph.elements.persistence.Path
import ch.datascience.graph.elements.persistence.impl.ImplPersistedRecordProperty

/**
  * Created by johann on 19/05/17.
  */
class PersistedRecordPropertyBuilder[Key, Value : BoxedOrValidValue] extends PropertyBuilder[Key, Value, ImplPersistedRecordProperty[Key, Value]] {

  private[this] var myParent: Option[Path] = None

  def parent: Option[Path] = myParent

  def parent_=(parent: Path): this.type = {
    myParent = Some(parent)
    this
  }

  def isReady: Boolean = (parent, key, value) match {
    case (Some(_), Some(_), Some(_)) => true
    case _ => false
  }

  def result(): ImplPersistedRecordProperty[Key, Value] = (parent, key, value) match {
    case (Some(p), Some(k), Some(v)) => ImplPersistedRecordProperty(p, k, v)
    case _ => notReady()
  }

}
