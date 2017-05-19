package ch.datascience.graph.elements.mappers

import ch.datascience.graph.elements.{BoxedOrValidValue, BoxedValue}
import ch.datascience.graph.elements.builders.PersistedRecordPropertyBuilder
import ch.datascience.graph.elements.persistence.impl.ImplPersistedRecordProperty
import org.apache.tinkerpop.gremlin.structure.{Property => GraphProperty}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 19/05/17.
  */
class PersistedRecordPropertyReader[Key : StringReader](implicit kvr: KeyValueReader[Key, BoxedValue])
  extends Reader[GraphProperty[java.lang.Object], PersistedRecordPropertyBuilder[Key, BoxedValue]] {

  def read(property: GraphProperty[java.lang.Object])(implicit ec: ExecutionContext): Future[PersistedRecordPropertyBuilder[Key, BoxedValue]] = {
    val builder = new PersistedRecordPropertyBuilder[Key, BoxedValue]()
    for {
      key <- implicitly[StringReader[Key]].read(property.key())
      value <- kvr.read(key -> property.value())
    } yield {
      builder.key = key
      builder.value = value
      builder
    }
  }

}
