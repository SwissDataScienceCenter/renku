package persistence.reader

import javax.inject.Inject

import ch.datascience.graph.elements.tinkerpop_mappers.{VertexReader => Base}
import persistence.scope.Scope

/**
  * Created by johann on 13/06/17.
  */
class VertexReader @Inject()(override val scope: Scope) extends Base(scope = scope)
