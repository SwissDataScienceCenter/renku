package ch.datascience.graph.elements.persisted.impl

import ch.datascience.graph.elements.persisted.{Path, PersistedVertexProperty}

/**
  * Created by johann on 30/05/17.
  */
private[persisted] case class ImplPersistedVertexProperty(
  id: PersistedVertexProperty#Id,
  parent: Path,
  key: PersistedVertexProperty#Key,
  value: PersistedVertexProperty#Value,
  properties: PersistedVertexProperty#Properties
) extends PersistedVertexProperty
