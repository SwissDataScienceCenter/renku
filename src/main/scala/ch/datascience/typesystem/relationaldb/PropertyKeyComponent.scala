package ch.datascience.typesystem.relationaldb

import java.util.UUID

import ch.datascience.typesystem.model.{PropertyKey => PropertyKeyModel}
import ch.datascience.typesystem.relationaldb.row.{GraphDomain, PropertyKey}
import ch.datascience.typesystem.{Cardinality, DataType}
import slick.lifted.{CompiledFunction, ForeignKeyQuery, Index, MappedProjection, ProvenShape}

/**
  * Created by johann on 17/03/17.
  */
trait PropertyKeyComponent { this: JdbcProfileComponent with SchemasComponent with ImplicitsComponent with EntityComponent with GraphDomainComponent with AbstractEntityComponent =>

  import profile.api._

  class PropertyKeys(tag: Tag) extends Table[PropertyKey](tag, "PROPERTY_KEYS") with AbstractEntityTable[PropertyKey] {

    // Columns
//    def id: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)

    def graphDomainId: Rep[UUID] = column[UUID]("GRAPH_DOMAIN_ID")

    def name: Rep[String] = column[String]("NAME")

    def dataType: Rep[DataType] = column[DataType]("DATA_TYPE")

    def cardinality: Rep[Cardinality] = column[Cardinality]("CARDINALITY")

    // Indexes
    def idx: Index = index("IDX_PROPERTY_KEYS_GRAPH_DOMAIN_ID_NAME", (graphDomainId, name), unique = true)

    // Foreign keys
//    def entity: ForeignKeyQuery[Entities, Entity] =
//      foreignKey("PROPERTY_KEYS_FK_ENTITIES", id, entities)(_.id)

    def graphDomain: ForeignKeyQuery[GraphDomains, GraphDomain] =
      foreignKey("PROPERTY_KEYS_FK_GRAPH_DOMAINS", graphDomainId, graphDomains)(_.id)

    // *
    def * : ProvenShape[PropertyKey] =
      (id, graphDomainId, name, dataType, cardinality) <> (PropertyKey.tupled, PropertyKey.unapply)

  }

  object propertyKeys extends TableQuery(new PropertyKeys(_)) with AbstractEntitiesTableQuery[PropertyKey, PropertyKeys] {

    lazy val withGraphDomain: Query[(GraphDomains, PropertyKeys), (GraphDomain, PropertyKey), Seq] = for {
      pk <- this
      gd <- pk.graphDomain
    } yield (gd, pk)

    lazy val withGraphDomainAsModel: Query[MappedProjection[PropertyKeyModel, (String, String, DataType, Cardinality)], PropertyKeyModel, Seq] = {
      for {
        (gd, pk) <- this.withGraphDomain
      } yield mapToModel(gd, pk)
    }

    lazy val findByIdAsModel: CompiledFunction[Rep[UUID] => Query[MappedProjection[PropertyKeyModel, (String, String, DataType, Cardinality)], PropertyKeyModel, Seq], Rep[UUID], UUID, Query[MappedProjection[PropertyKeyModel, (String, String, DataType, Cardinality)], PropertyKeyModel, Seq], Seq[PropertyKeyModel]] = Compiled {
      (entityId: Rep[UUID]) => for {
        (gd, pk) <- this.withGraphDomain
        if pk.id === entityId
      } yield mapToModel(gd, pk)
    }

    lazy val findByNamespaceAndName: CompiledFunction[(Rep[String], Rep[String]) => Query[PropertyKeys, PropertyKey, Seq], (Rep[String], Rep[String]), (String, String), Query[PropertyKeys, PropertyKey, Seq], Seq[PropertyKey]] = Compiled {
      (namespace: Rep[String], name: Rep[String]) => for {
        (gd, pk) <- this.withGraphDomain
        if gd.namespace === namespace && pk.name === name
      } yield pk
    }

    lazy val findByNamespaceAndNameAsModel = Compiled {
      (namespace: Rep[String], name: Rep[String]) => for {
        (gd, pk) <- this.withGraphDomain
        if gd.namespace === namespace && pk.name === name
      } yield mapToModel(gd, pk)
    }


    private[this] def mapToModel(gd: GraphDomains, pk: PropertyKeys) = (gd.namespace, pk.name, pk.dataType, pk.cardinality).mapTo[PropertyKeyModel]

  }

  _schemas += propertyKeys.schema

}
