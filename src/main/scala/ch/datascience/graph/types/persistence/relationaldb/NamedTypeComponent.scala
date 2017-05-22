package ch.datascience.graph.types.persistence.relationaldb

import java.time.Instant
import java.util.UUID

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.types.{Cardinality, DataType}
import ch.datascience.graph.types.persistence.model._
import ch.datascience.graph.types.persistence.model.relational._
import slick.lifted._

import scala.collection.IterableLike
import scala.concurrent.{ExecutionContext, Future}
import scala.language.{higherKinds, implicitConversions}

/**
  * Created by johann on 15/05/17.
  */
trait NamedTypeComponent { this: JdbcProfileComponent with SchemasComponent with ImplicitsComponent with EntityComponent with GraphDomainComponent with PropertyKeyComponent with AbstractEntityComponent =>

  import profile.api._

  class NamedTypes(tag: Tag) extends Table[RowNamedType](tag, "NAMED_TYPES") with AbstractEntityTable[RowNamedType] {

    // Columns
    // def id: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)

    def graphDomainId: Rep[UUID] = column[UUID]("GRAPH_DOMAIN_ID")

    def name: Rep[String] = column[String]("NAME")

    // Indexes
    def idx: Index = index("IDX_NAMED_TYPES_GRAPH_DOMAIN_ID_NAME", (graphDomainId, name), unique = true)

    // Foreign keys
    //    def entity: ForeignKeyQuery[Entities, Entity] =
    //      foreignKey("PROPERTY_KEYS_FK_ENTITIES", id, entities)(_.id)

    def graphDomain: ForeignKeyQuery[GraphDomains, RowGraphDomain] =
      foreignKey("NAMED_TYPES_FK_GRAPH_DOMAINS", graphDomainId, graphDomains)(_.id)

    // *
    def * : ProvenShape[RowNamedType] =
      (id, graphDomainId, name) <> (RowNamedType.tupled, RowNamedType.unapply)


    def mappedUsing(graphDomains: GraphDomains): MappedProjection[NamedTypeBasic, (UUID, GraphDomain, String)] = {
      (id, graphDomains.mapped, name).mapTo[NamedTypeBasic]
    }

  }

  final class RichNamedTypesQuery[C[A] <: Seq[A]](self: Query[NamedTypes, RowNamedType, C]) {

    def withGraphDomain: Query[(GraphDomains, NamedTypes), (RowGraphDomain, RowNamedType), C] = for {
      nt <- self
      gd <- nt.graphDomain
    } yield (gd, nt)

    def mapped: Query[MappedProjection[NamedTypeBasic, (UUID, GraphDomain, String)], NamedTypeBasic, C] = for {
      (gd, nt) <- self.withGraphDomain
    } yield nt.mappedUsing(gd)

    def withSuperTypes: Query[(Rep[UUID], (NamedTypes, Rep[String])), (UUID, (RowNamedType, String)), C] = for {
      nt <- self
      link <- namedTypeLinks
      superType <- link.parentNamedType
      superTypeGD <- superType.graphDomain
      if nt.id === link.namedTypeId
    } yield (nt.id, (superType, superTypeGD.namespace))

//    def withProperties: Query[(Rep[UUID], (PropertyKeys, Rep[String])), (UUID, (RowPropertyKey, String)), C] = for {
//      nt <- self
//      link <- namedTypeProperties
//      property <- link.propertyKey
//      propertyGD <- property.graphDomain
//      if nt.id === link.namedTypeId
//    } yield (nt.id, (property, propertyGD.namespace))

    def withProperties: Query[(Rep[UUID], MappedProjection[PropertyKey, (UUID, GraphDomain, String, DataType, Cardinality)]), (UUID, PropertyKey), C] = for {
      nt <- self
      link <- namedTypeProperties
      property <- link.propertyKey
      gd <- property.graphDomain
      if nt.id === link.namedTypeId
    } yield (nt.id, property.mappedUsing(gd))

    def runNamedTypeQuery(implicit ec: ExecutionContext): Future[Seq[NamedType]] = {
      val futureNamedTypes = db.run( this.mapped.result )
      val futureSuperTypes = runOneToManyQuery( this.withSuperTypes )
      val futureProperties = runOneToManyQuery( this.withProperties )

      val joined = for {
        seq <- futureNamedTypes
        mapSuperTypes <- futureSuperTypes
        mapProperties <- futureProperties
      } yield for {
        namedType <- seq
      } yield (namedType, mapSuperTypes.getOrElse(namedType.id, Seq.empty), mapProperties.getOrElse(namedType.id, Seq.empty))

      for {
        seq <- joined
      } yield for {
        (namedType, superTypesSeq, propertiesSeq) <- seq
        superTypes = for { (superType, namespace) <- superTypesSeq } yield NamespaceAndName(namespace, superType.name) -> superType
        properties = for { property <- propertiesSeq } yield property.key -> property
      } yield NamedType(namedType.id, namedType.graphDomain, namedType.name, superTypes.toMap, properties.toMap)
    }

    def runOneToManyQuery[OneTable, ManyTable, OneRow, ManyRow](query: Query[(OneTable, ManyTable), (OneRow, ManyRow), C])(implicit ec: ExecutionContext): Future[Map[OneRow, Seq[ManyRow]]] = {
      val futureSeq = db.run( query.result )

      for {
        seq <- futureSeq
      } yield for {
        (one, group) <- seq groupBy { case (one, _) => one }
        manySeq = for {
          (_, many) <- group
        } yield many
      } yield one -> manySeq
    }
  }

  implicit def toRichNamedTypesQuery[C[A] <: Seq[A]](query: Query[NamedTypes, RowNamedType, C]): RichNamedTypesQuery[C] = new RichNamedTypesQuery(query)

  object namedTypes extends TableQuery(new NamedTypes(_)) with AbstractEntitiesTableQuery[RowNamedType, NamedType, NamedTypes] {

    type MappedQuery = Query[MappedProjection[NamedTypeBasic, (UUID, GraphDomain, String)], NamedTypeBasic, Seq]

    lazy val findById: CompiledFunction[Rep[UUID] => MappedQuery, Rep[UUID], UUID, MappedQuery, Seq[NamedTypeBasic]] = Compiled {
      this.findRowById.extract andThen {_.mapped }
    }

    lazy val findRowByNamespaceAndName: CompiledFunction[(Rep[String], Rep[String]) => Query[NamedTypes, RowNamedType, Seq], (Rep[String], Rep[String]), (String, String), Query[NamedTypes, RowNamedType, Seq], Seq[RowNamedType]] = Compiled {
      (namespace: Rep[String], name: Rep[String]) => for {
        (gd, nt) <- this.withGraphDomain
        if gd.namespace === namespace && nt.name === name
      } yield nt
    }

    lazy val findByNamespaceAndName: CompiledFunction[(Rep[String], Rep[String]) => MappedQuery, (Rep[String], Rep[String]), (String, String), MappedQuery, Seq[NamedTypeBasic]] = Compiled {
      (namespace: Rep[String], name: Rep[String]) => for {
        (gd, nt) <- this.withGraphDomain
        if gd.namespace === namespace && nt.name === name
      } yield nt.mappedUsing(gd)
    }

    override def insertEntity(namedType: NamedType): DBIO[Unit] = {
      val addEntity = super.insertEntity(namedType)
      val addSuperTypes = namedTypeLinks ++= namedType.superTypesMap.values.map(x => RowNamedTypeLink(namedType.id, x.id))
      val addProperties = namedTypeProperties ++= namedType.propertiesMap.values.map(x => RowNamedTypeProperty(namedType.id, x.id))
      DBIO.seq(addEntity, addSuperTypes, addProperties)
    }

  }

  class NamedTypeLinks(tag: Tag) extends Table[RowNamedTypeLink](tag, "NAMED_TYPES_HIERARCHY") {

    // Columns
    def namedTypeId: Rep[UUID] = column[UUID]("NAMED_TYPE_ID")

    def parentNamedTypeId: Rep[UUID] = column[UUID]("PARENT_NAMED_TYPE_ID")

    // Indexes
    def pk: PrimaryKey = primaryKey("IDX_NAMED_TYPES_HIERARCHY", (namedTypeId, parentNamedTypeId))

    // Foreign keys
    def namedType: ForeignKeyQuery[NamedTypes, RowNamedType] =
      foreignKey("NAMED_TYPES_HIERARCHY_FK_NAMED_TYPES", namedTypeId, namedTypes)(_.id)

    def parentNamedType: ForeignKeyQuery[NamedTypes, RowNamedType] =
      foreignKey("NAMED_TYPES_HIERARCHY_PARENT_FK_NAMED_TYPES", parentNamedTypeId, namedTypes)(_.id)

    // *
    def * : ProvenShape[RowNamedTypeLink] =
      (namedTypeId, parentNamedTypeId) <> (RowNamedTypeLink.tupled, RowNamedTypeLink.unapply)

  }

  object namedTypeLinks extends TableQuery(new NamedTypeLinks(_))

  class NamedTypeProperties(tag: Tag) extends Table[RowNamedTypeProperty](tag, "NAMED_TYPES_PROPERTIES") {

    // Columns
    def namedTypeId: Rep[UUID] = column[UUID]("NAMED_TYPE_ID")

    def propertyKeyId: Rep[UUID] = column[UUID]("PROPERTY_KEY_ID")

    // Indexes
    def pk: PrimaryKey = primaryKey("IDX_NAMED_TYPES_PROPERTIES", (namedTypeId, propertyKeyId))

    // Foreign keys
    def namedType: ForeignKeyQuery[NamedTypes, RowNamedType] =
      foreignKey("NAMED_TYPES_PROPERTIES_FK_NAMED_TYPES", namedTypeId, namedTypes)(_.id)

    def propertyKey: ForeignKeyQuery[PropertyKeys, RowPropertyKey] =
      foreignKey("NAMED_TYPES_PROPERTIES_FK_PROPERTY_KEYS", propertyKeyId, propertyKeys)(_.id)

    // *
    def * : ProvenShape[RowNamedTypeProperty] =
      (namedTypeId, propertyKeyId) <> (RowNamedTypeProperty.tupled, RowNamedTypeProperty.unapply)

  }

  object namedTypeProperties extends TableQuery(new NamedTypeProperties(_))

  _schemas ++= Seq(
    namedTypes.schema,
    namedTypeLinks.schema,
    namedTypeProperties.schema
  )

}
