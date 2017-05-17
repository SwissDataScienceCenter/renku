package ch.datascience.graph.scope.persistence.remote

import ch.datascience.graph.scope.persistence.PersistenceLayer
import play.api.libs.json.Reads

import scala.concurrent.ExecutionContext

/**
  * Created by johann on 17/05/17.
  */
class RemotePersistenceLayer[TypeKey : Reads, PropKey : Reads](val client: ConfiguredClient[TypeKey, PropKey])(implicit val executionContext: ExecutionContext)
  extends PersistenceLayer[TypeKey, PropKey]
    with RemotePersistedProperties[PropKey]
    with RemotePersistedNamedTypes[TypeKey, PropKey] {

  protected def keyReads: Reads[PropKey] = implicitly[Reads[PropKey]]

}
