package ch.datascience.graph.elements.persisted

import ch.datascience.graph.Constants
import ch.datascience.graph.elements.persisted.impl.ImplPersistedVertexProperty

/**
  * Created by johann on 30/05/17.
  */
trait PersistedVertexProperty extends PersistedMultiRecordRichProperty {

  type Id = Constants.VertexPropertyId

  final type PathType = VertexPropertyPath

  final def path: VertexPropertyPath = VertexPropertyPath(parent, id)

}

object PersistedVertexProperty {

  def apply(
    id: PersistedVertexProperty#Id,
    parent: Path,
    key: PersistedVertexProperty#Key,
    value: PersistedVertexProperty#Value,
    properties: PersistedVertexProperty#Properties
  ): PersistedVertexProperty = ImplPersistedVertexProperty(id, parent, key, value, properties)

  def unapply(prop: PersistedVertexProperty): Option[(PersistedVertexProperty#Id, Path, PersistedVertexProperty#Key, PersistedVertexProperty#Value, PersistedVertexProperty#Properties)] = {
    if (prop eq null)
      None
    else
      Some(prop.id, prop.parent, prop.key, prop.value, prop.properties)
  }

}
