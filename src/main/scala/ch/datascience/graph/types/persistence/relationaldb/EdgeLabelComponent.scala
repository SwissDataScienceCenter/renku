package ch.datascience.graph.types.persistence.relationaldb

import java.util.UUID

import ch.datascience.graph.types.Multiplicity
import ch.datascience.graph.types.persistence.model.{EdgeLabel, GraphDomain, RichEdgeLabel}
import slick.lifted._

import scala.language.{higherKinds, implicitConversions}

/**
  * Created by johann on 07/06/17.
  */
trait EdgeLabelComponent { this: JdbcProfileComponent with SchemasComponent with ImplicitsComponent with EntityComponent with GraphDomainComponent  with AbstractEntityComponent =>

  import profile.api._

  class EdgeLabels(tag: Tag) extends Table[EdgeLabel](tag, "EDGE_LABELS") with AbstractEntityTable[EdgeLabel] {

    // Columns
    //    def id: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)

    def graphDomainId: Rep[UUID] = column[UUID]("GRAPH_DOMAIN_ID")

    def name: Rep[String] = column[String]("NAME")

    def multiplicity: Rep[Multiplicity] = column[Multiplicity]("MULTIPLICITY")

    // Indexes
    def idx: Index = index("IDX_EDGE_LABELS_GRAPH_DOMAIN_ID_NAME", (graphDomainId, name), unique = true)

    // Foreign keys
    //    def entity: ForeignKeyQuery[Entities, Entity] =
    //      foreignKey("GRAPH_DOMAINS_FK_ENTITIES", id, entities)(_.id)

    def graphDomain: ForeignKeyQuery[GraphDomains, GraphDomain] =
      foreignKey("EDGE_LABELS_FK_GRAPH_DOMAINS", graphDomainId, graphDomains)(_.id)

    // *
    def * : ProvenShape[EdgeLabel] = (id, graphDomainId, name, multiplicity) <> (EdgeLabel.tupled, EdgeLabel.unapply)

  }

  final class RichEdgeLabelsQuery[C[_]](self: Query[EdgeLabels, EdgeLabel, C]) {

    def withGraphDomain: Query[(GraphDomains, EdgeLabels), (GraphDomain, EdgeLabel), C] = for {
      el <- self
      gd <- el.graphDomain
    } yield (gd, el)

    def mapped: Query[MappedProjection[RichEdgeLabel, (GraphDomain, EdgeLabel)], RichEdgeLabel, C] = for {
      (gd, el) <- this.withGraphDomain
    } yield (gd, el) <> (to, from)

    protected def to(t: (GraphDomain, EdgeLabel)): RichEdgeLabel = t._2.toRichEdgeLabel(t._1)

    protected def from(edgeLabel: RichEdgeLabel): Option[(GraphDomain, EdgeLabel)] = RichEdgeLabel.unapply(edgeLabel) match {
      case Some((_, gd, _, _)) => Some((gd, edgeLabel))
      case _ => None
    }

  }

  implicit def toRichEdgeLabelsQuery[C[_]](query: Query[EdgeLabels, EdgeLabel, C]): RichEdgeLabelsQuery[C] = new RichEdgeLabelsQuery(query)

  object edgeLabels extends TableQuery(new EdgeLabels(_)) with AbstractEntitiesTableQuery[EdgeLabel, RichEdgeLabel, EdgeLabels] {

    lazy val findById: CompiledFunction[Rep[UUID] => Query[MappedProjection[RichEdgeLabel, (GraphDomain, EdgeLabel)], RichEdgeLabel, Seq], Rep[UUID], UUID, Query[MappedProjection[RichEdgeLabel, (GraphDomain, EdgeLabel)], RichEdgeLabel, Seq], Seq[RichEdgeLabel]] = Compiled {
      this.findBy(_.id).extract andThen { _.mapped }
    }

    lazy val findByNamespaceAndName: CompiledFunction[(Rep[String], Rep[String]) => Query[MappedProjection[RichEdgeLabel, (GraphDomain, EdgeLabel)], RichEdgeLabel, Seq], (Rep[String], Rep[String]), (String, String), Query[MappedProjection[RichEdgeLabel, (GraphDomain, EdgeLabel)], RichEdgeLabel, Seq], Seq[RichEdgeLabel]] = Compiled {
      (namespace: Rep[String], name: Rep[String]) => (for {
        (gd, el) <- this.withGraphDomain
        if gd.namespace === namespace && el.name === name
      } yield el).mapped
    }

  }

  _schemas += edgeLabels.schema

}
