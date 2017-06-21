package ch.datascience.graph.scope.persistence.remote

import ch.datascience.graph.scope.persistence.PersistenceLayer
import ch.datascience.graph.types.PropertyKey
import ch.datascience.graph.types.json.propKeyFormat
import play.api.libs.json.Reads

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 17/05/17.
  */
class RemotePersistenceLayer(val client: ConfiguredClient)(implicit val executionContext: ExecutionContext)
  extends PersistenceLayer
    with RemotePersistedProperties
    with RemotePersistedNamedTypes {

  protected def keyReads: Reads[PropertyKey#Key] = propKeyFormat

}
