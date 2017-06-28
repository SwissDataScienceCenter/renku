package ch.datascience.graph.types.persistence.orchestration

import java.util.UUID

import ch.datascience.graph.types.persistence.model.SystemPropertyKey
import ch.datascience.graph.types.{Cardinality, DataType}

import scala.concurrent.Future

/**
  * Created by johann on 04/04/17.
  */
trait SystemPropertyKeyComponent {
  this: ExecutionComponent with DatabaseComponent with GraphComponent =>

  import profile.api._

  object systemPropertyKeys {

    def all(): Future[Seq[SystemPropertyKey]] = {
      db.run( dal.systemPropertyKeys.result )
    }

    def findById(id: UUID): Future[Option[SystemPropertyKey]] = {
      db.run( dal.systemPropertyKeys.findById(id).result.headOption )
    }

    def findByName(name: String): Future[Option[SystemPropertyKey]] = {
      db.run( dal.systemPropertyKeys.findByName(name).result.headOption )
    }

    def createSystemPropertyKey(
      name: String,
      dataType: DataType = DataType.String,
      cardinality: Cardinality = Cardinality.Single
    ): Future[SystemPropertyKey] = {
      val propertyKey = SystemPropertyKey(UUID.randomUUID(), name, dataType, cardinality)
      val insertPropertyKey = dal.systemPropertyKeys add propertyKey
      val propagateChange = insertPropertyKey flatMap { _ =>
        val future = gdb.run(gal.propertyKeys.addPropertyKey(name, dataType, cardinality)).map(_ => propertyKey)
        DBIO.from(future)
      }
      db.run(propagateChange.transactionally)
    }

  }

}
