package ch.datascience.typesystem
package relationaldb

import ch.datascience.typesystem.model.relational.GraphDomain
import slick.lifted.{CompiledFunction, Index, ProvenShape}

/**
  * Created by johann on 17/03/17.
  */
trait GraphDomainComponent { this: JdbcProfileComponent with SchemasComponent with EntityComponent with AbstractEntityComponent =>

  import profile.api._

  class GraphDomains(tag: Tag) extends Table[GraphDomain](tag, "GRAPH_DOMAINS") with AbstractEntityTable[GraphDomain] {

    // Columns
//    def id: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)

    def namespace: Rep[String] = column[String]("NAMESPACE")

    // Indexes
    def idx: Index = index("IDX_GRAPH_DOMAINS_NAMESPACE", namespace, unique = true)

    // Foreign keys
//    def entity: ForeignKeyQuery[Entities, Entity] =
//      foreignKey("GRAPH_DOMAINS_FK_ENTITIES", id, entities)(_.id)

    // *
    def * : ProvenShape[GraphDomain] = (id, namespace) <> (GraphDomain.tupled, GraphDomain.unapply)

  }

  object graphDomains extends TableQuery(new GraphDomains(_)) with AbstractEntitiesTableQuery[GraphDomain, GraphDomains] {

    val findByNamespace: CompiledFunction[Rep[String] => Query[GraphDomains, GraphDomain, Seq], Rep[String], String, Query[GraphDomains, GraphDomain, Seq], Seq[GraphDomain]] =
      this.findBy(_.namespace)

  }

  _schemas += graphDomains.schema

}
