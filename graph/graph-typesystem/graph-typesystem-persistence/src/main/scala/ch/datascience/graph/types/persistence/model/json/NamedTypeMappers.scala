package ch.datascience.graph.types.persistence.model.json

import java.util.UUID

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.naming.json.{NameFormat, NamespaceAndNameFormat, NamespaceFormat}
import ch.datascience.graph.types.persistence.model.{GraphDomain, RichNamedType, RichPropertyKey}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath, Reads, Writes}

/**
  * Created by johann on 13/06/17.
  */
object NamedTypeMappers {

  lazy val NamedTypeFormat: Format[RichNamedType] = (
    (JsPath \ "id").format[UUID](UUIDFormat) and
      (JsPath \ "graph_domain").format[GraphDomain](GraphDomainFormat) and
      (JsPath \ "name").format[String](NameFormat) and
      (JsPath \ "super_types").format[Iterable[RichNamedType]](superTypesFormat) and
      (JsPath \ "properties").format[Iterable[RichPropertyKey]](propertiesFormat)
  )(RichNamedType.apply, { nt => (nt.id, nt.graphDomain, nt.name, nt.superTypes.values, nt.properties.values) })

  lazy val NamedTypeRequestFormat: Format[(String, String, Seq[NamespaceAndName], Seq[NamespaceAndName])] = (
    (JsPath \ "namespace").format[String](NamespaceFormat) and
      (JsPath \ "name").format[String](NameFormat) and
      (JsPath \ "super_types").format[Seq[NamespaceAndName]](seqNamespaceAndNameFormatFormat) and
      (JsPath \ "properties").format[Seq[NamespaceAndName]](seqNamespaceAndNameFormatFormat)
  )(Tuple4.apply, unlift(Tuple4.unapply))

  private[this] lazy val NestedNamedTypeFormat: Format[RichNamedType] = (
    (JsPath \ "id").format[UUID](UUIDFormat) and
      (JsPath \ "graph_domain").format[GraphDomain](GraphDomainFormat) and
      (JsPath \ "name").format[String](NameFormat)
    )({ (id, gd, name) => RichNamedType(id, gd, name, Seq.empty, Seq.empty) }, { nt => (nt.id, nt.graphDomain, nt.name) })

  private[this] lazy val superTypesFormat: Format[Iterable[RichNamedType]] = {
    Format(Reads.seq(NestedNamedTypeFormat).map(x => x: Iterable[RichNamedType]), Writes.traversableWrites(NestedNamedTypeFormat))
  }

  private[this] lazy val propertiesFormat: Format[Iterable[RichPropertyKey]] = {
    Format(Reads.seq(PropertyKeyFormat).map(x => x: Iterable[RichPropertyKey]), Writes.traversableWrites(PropertyKeyFormat))
  }

  private[this] lazy val seqNamespaceAndNameFormatFormat: Format[Seq[NamespaceAndName]] = {
    Format(Reads.seq(NamespaceAndNameFormat), Writes.seq(NamespaceAndNameFormat))
  }

}
