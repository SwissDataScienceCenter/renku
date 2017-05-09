package ch.datascience.graph.types.persistence.relationaldb

import java.util.UUID

import ch.datascience.graph.types.persistence.model.GraphDomain
import ch.datascience.graph.types.persistence.model.relational.RowGraphDomain
import slick.lifted.{CompiledFunction, Index, MappedProjection, ProvenShape}

import scala.language.{higherKinds, implicitConversions}

/**
  * Created by johann on 17/03/17.
  */
trait GraphDomainComponent { this: JdbcProfileComponent with SchemasComponent with EntityComponent with AbstractEntityComponent =>

  import profile.api._

  class GraphDomains(tag: Tag) extends Table[RowGraphDomain](tag, "GRAPH_DOMAINS") with AbstractEntityTable[RowGraphDomain] {

    // Columns
//    def id: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)

    def namespace: Rep[String] = column[String]("NAMESPACE")

    // Indexes
    def idx: Index = index("IDX_GRAPH_DOMAINS_NAMESPACE", namespace, unique = true)

    // Foreign keys
//    def entity: ForeignKeyQuery[Entities, Entity] =
//      foreignKey("GRAPH_DOMAINS_FK_ENTITIES", id, entities)(_.id)

    // *
    def * : ProvenShape[RowGraphDomain] = (id, namespace) <> (RowGraphDomain.tupled, RowGraphDomain.unapply)

    def mapped: MappedProjection[GraphDomain, (UUID, String)] = (id, namespace).mapTo[GraphDomain]

  }

  final class RichGraphDomainsQuery[C[_]](self: Query[GraphDomains, RowGraphDomain, C]) {

    def mapped: Query[MappedProjection[GraphDomain, (UUID, String)], GraphDomain, C] = for {
      gd <- self
    } yield gd.mapped

  }

  implicit def toRichGraphDomainsQuery[C[_]](query: Query[GraphDomains, RowGraphDomain, C]): RichGraphDomainsQuery[C] = new RichGraphDomainsQuery(query)

  object graphDomains extends TableQuery(new GraphDomains(_)) with AbstractEntitiesTableQuery[RowGraphDomain, GraphDomain, GraphDomains] {

    type MappedQuery = Query[MappedProjection[GraphDomain, (UUID, String)], GraphDomain, Seq]

    lazy val findById: CompiledFunction[Rep[UUID] => MappedQuery, Rep[UUID], UUID, MappedQuery, Seq[GraphDomain]] = Compiled {
      this.findRowById.extract andThen {_.mapped }
    }

    lazy val findRowByNamespace: CompiledFunction[Rep[String] => Query[GraphDomains, RowGraphDomain, Seq], Rep[String], String, Query[GraphDomains, RowGraphDomain, Seq], Seq[RowGraphDomain]] = {
      this.findBy(_.namespace)
    }

    lazy val findByNamespace: CompiledFunction[Rep[String] => MappedQuery, Rep[String], String, MappedQuery, Seq[GraphDomain]] = Compiled {
      this.findRowByNamespace.extract andThen { _.mapped }
    }

  }

  _schemas += graphDomains.schema

}
