package ch.datascience.graph.elements.mappers

import ch.datascience.graph.elements.BoxedValue
import ch.datascience.graph.elements.persistence.Path
import ch.datascience.graph.elements.persistence.impl.ImplPersistedRecordProperty
import org.apache.tinkerpop.gremlin.structure.{Property => GraphProperty}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 19/05/17.
  */
class PersistedRecordPropertyReader[Key : StringReader](val parent: Path)(implicit kvr: KeyValueReader[Key, BoxedValue])
  extends Reader[GraphProperty[java.lang.Object], ImplPersistedRecordProperty[Key, BoxedValue]] {

  def read(property: GraphProperty[java.lang.Object])(implicit ec: ExecutionContext): Future[ImplPersistedRecordProperty[Key, BoxedValue]] = {
    for {
      key <- implicitly[StringReader[Key]].read(property.key())
      value <- kvr.read(key -> property.value())
    } yield {
      ImplPersistedRecordProperty(parent, key, value)
    }
  }

}
