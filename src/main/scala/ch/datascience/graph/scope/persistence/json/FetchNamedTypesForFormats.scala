package ch.datascience.graph.scope.persistence.json

import ch.datascience.graph.naming.json.NamespaceAndNameFormat
import ch.datascience.graph.types.NamedType
import ch.datascience.graph.types.json.NamedTypeFormat
import play.api.libs.json._

/**
  * Created by johann on 19/06/17.
  */
object FetchNamedTypesForFormats {

  object QueryFormat extends Format[Set[NamedType#TypeId]] {

    def writes(keys: Set[NamedType#TypeId]): JsValue = seqWrites.writes(keys.toSeq)

    def reads(json: JsValue): JsResult[Set[NamedType#TypeId]] = for { seq <- seqReads.reads(json) } yield seq.toSet

    private[this] lazy val seqWrites = Writes.seq(NamespaceAndNameFormat)

    private[this] lazy val seqReads = Reads.seq(NamespaceAndNameFormat)

  }

  object ResponseFormat extends Format[Map[NamedType#TypeId, NamedType]] {

    def writes(definitions: Map[NamedType#TypeId, NamedType]): JsValue = seqWrites.writes(definitions.values)

    def reads(json: JsValue): JsResult[Map[NamedType#TypeId, NamedType]] = seqReads.reads(json) map { seq =>
      val withKey = for {
        namedType <- seq
      } yield namedType.typeId -> namedType
      withKey.toMap
    }

    private[this] lazy val seqWrites = Writes.traversableWrites(NamedTypeFormat)

    private[this] lazy val seqReads = Reads.seq(NamedTypeFormat)

  }

}
