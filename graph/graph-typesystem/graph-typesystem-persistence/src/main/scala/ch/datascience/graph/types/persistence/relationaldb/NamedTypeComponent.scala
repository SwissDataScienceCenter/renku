package ch.datascience.graph.types.persistence.relationaldb

import java.time.Instant
import java.util.UUID

import ch.datascience.graph.naming.NamespaceAndName
import ch.datascience.graph.types.{Cardinality, DataType}
import ch.datascience.graph.types.persistence.model._
import slick.lifted._

import scala.collection.IterableLike
import scala.concurrent.{ExecutionContext, Future}
import scala.language.{higherKinds, implicitConversions}

/**
  * Created by johann on 15/05/17.
  */
trait NamedTypeComponent { this: JdbcProfileComponent with SchemasComponent with ImplicitsComponent with EntityComponent with GraphDomainComponent with PropertyKeyComponent with AbstractEntityComponent =>

  import profile.api._

  class NamedTypes(tag: Tag) extends Table[NamedType](tag, "NAMED_TYPES") with AbstractEntityTable[NamedType] {

    // Columns
    // def id: Rep[UUID] = column[UUID]("UUID", O.PrimaryKey)

    def graphDomainId: Rep[UUID] = column[UUID]("GRAPH_DOMAIN_ID")

    def name: Rep[String] = column[String]("NAME")

    // Indexes
    def idx: Index = index("IDX_NAMED_TYPES_GRAPH_DOMAIN_ID_NAME", (graphDomainId, name), unique = true)

    // Foreign keys
    //    def entity: ForeignKeyQuery[Entities, Entity] =
    //      foreignKey("PROPERTY_KEYS_FK_ENTITIES", id, entities)(_.id)

    def graphDomain: ForeignKeyQuery[GraphDomains, GraphDomain] =
      foreignKey("NAMED_TYPES_FK_GRAPH_DOMAINS", graphDomainId, graphDomains)(_.id)

    // *
    def * : ProvenShape[NamedType] =
      (id, graphDomainId, name) <> (NamedType.tupled, NamedType.unapply)


//    def mappedUsing(graphDomains: GraphDomains): MappedProjection[NamedTypeBasic, (UUID, GraphDomain, String)] = {
//      (id, graphDomains.mapped, name).mapTo[NamedTypeBasic]
//    }

  }

  final class RichNamedTypesQuery[C[A] <: Seq[A]](self: Query[NamedTypes, NamedType, C]) {

    def withGraphDomain: Query[(GraphDomains, NamedTypes), (GraphDomain, NamedType), C] = for {
      nt <- self
      gd <- nt.graphDomain
    } yield (gd, nt)

    def withSuperTypes: Query[(Rep[UUID], MappedProjection[RichNamedType, (GraphDomain, NamedType)]), (UUID, RichNamedType), C] = for {
      nt <- self
      link <- namedTypeLinks
      if nt.id === link.namedTypeId
      parent <- link.parentNamedType.halfMapped
    } yield (nt.id, parent)

    def withProperties: Query[(Rep[UUID], MappedProjection[RichPropertyKey, (GraphDomain, PropertyKey)]), (UUID, RichPropertyKey), C] = for {
      nt <- self
      link <- namedTypeProperties
      if nt.id === link.namedTypeId
      property <- link.propertyKey.mapped
    } yield (nt.id, property)

    def halfMapped: Query[MappedProjection[RichNamedType, (GraphDomain, NamedType)], RichNamedType, C] = for {
      (gd, pk) <- this.withGraphDomain
    } yield (gd, pk) <> (to, from)

    protected def to(t: (GraphDomain, NamedType)): RichNamedType = t._2.toRichNamedType(t._1, Map.empty, Map.empty)

    protected def from(namedType: RichNamedType): Option[(GraphDomain, NamedType)] = RichNamedType.unapply(namedType) match {
      case Some((_, gd, _, _, _)) => Some((gd, namedType))
      case _ => None
    }

  }

  implicit def toRichNamedTypesQuery[C[A] <: Seq[A]](query: Query[NamedTypes, NamedType, C]): RichNamedTypesQuery[C] = new RichNamedTypesQuery(query)

  object namedTypes extends TableQuery(new NamedTypes(_)) with AbstractEntitiesTableQuery[NamedType, RichNamedType, NamedTypes] {

    def all()(implicit ec: ExecutionContext): Future[Seq[RichNamedType]] = {
      val f1 = db.run( this.halfMapped.result )
      val f2 = db.run( this.withSuperTypes.result )
      val f3 = db.run( this.withProperties.result )
      joinFutures(f1, f2, f3)
    }

    def findById(id: UUID)(implicit ec: ExecutionContext): Future[Seq[RichNamedType]] = {
      val f1 = db.run( this.findByIdHalfMapped(id).result )
      val f2 = db.run( this.findByIdWithSuperTypes(id).result )
      val f3 = db.run( this.findByIdWithProperties(id).result )
      joinFutures(f1, f2, f3)
    }

    private lazy val findByIdBase = this.findBy(_.id).extract

    private lazy val findByIdHalfMapped = Compiled {
      findByIdBase andThen { _.halfMapped }
    }

    private lazy val findByIdWithSuperTypes = Compiled {
      findByIdBase andThen { _.withSuperTypes }
    }

    private lazy val findByIdWithProperties = Compiled {
      findByIdBase andThen { _.withProperties }
    }

    def findByNamespaceAndName(namespace: String, name: String)(implicit ec: ExecutionContext): Future[Seq[RichNamedType]] = {
      val f1 = db.run( this.findByNamespaceAndNameHalfMapped((namespace, name)).result )
      val f2 = db.run( this.findByNamespaceAndNameWithSuperTypes((namespace, name)).result )
      val f3 = db.run( this.findByNamespaceAndNameWithProperties((namespace, name)).result )
      joinFutures(f1, f2, f3)
    }

    private lazy val findByNamespaceAndNameBase = {
      (namespace: Rep[String], name: Rep[String]) => for {
        (gd, pk) <- this.withGraphDomain
        if gd.namespace === namespace && pk.name === name
      } yield pk
    }

    private lazy val findByNamespaceAndNameHalfMapped = Compiled {
      findByNamespaceAndNameBase.tupled andThen { _.halfMapped }
    }

    private lazy val findByNamespaceAndNameWithSuperTypes = Compiled {
      findByNamespaceAndNameBase.tupled andThen { _.withSuperTypes }
    }

    private lazy val findByNamespaceAndNameWithProperties = Compiled {
      findByNamespaceAndNameBase.tupled andThen { _.withProperties }
    }

    override def insertEntity(namedType: RichNamedType): DBIO[Unit] = {
      val addEntity = super.insertEntity(namedType)
      val addSuperTypes = namedTypeLinks ++= namedType.superTypes.values.map(x => NamedTypeLink(namedType.id, x.id))
      val addProperties = namedTypeProperties ++= namedType.properties.values.map(x => NamedTypeProperty(namedType.id, x.id))
      DBIO.seq(addEntity, addSuperTypes, addProperties)
    }

    protected def joinFutures(f1: Future[Seq[RichNamedType]], f2: Future[Seq[(UUID, RichNamedType)]], f3: Future[Seq[(UUID, RichPropertyKey)]])(implicit ec: ExecutionContext): Future[Seq[RichNamedType]] = {
      for {
        namedTypes <- f1
        superTypes <- f2
        properties <- f3
        superTypesMap = groupByKey(superTypes)
        propertiesMap = groupByKey(properties)
      } yield for {
        namedType <- namedTypes
      } yield {
        RichNamedType(namedType.id, namedType.graphDomain, namedType.name, superTypesMap.getOrElse(namedType.id, Seq.empty), propertiesMap.getOrElse(namedType.id, Seq.empty))
      }
    }

    protected def groupByKey[A, B](it: Iterable[(A, B)]): Map[A, Iterable[B]] = {
      val grouped = it.groupBy(_._1)
      for {
        (key, seq) <- grouped
      } yield key -> {
        for {
          (_, value) <- seq
        } yield value
      }
    }

  }

  class NamedTypeLinks(tag: Tag) extends Table[NamedTypeLink](tag, "NAMED_TYPES_HIERARCHY") {

    // Columns
    def namedTypeId: Rep[UUID] = column[UUID]("NAMED_TYPE_ID")

    def parentNamedTypeId: Rep[UUID] = column[UUID]("PARENT_NAMED_TYPE_ID")

    // Indexes
    def pk: PrimaryKey = primaryKey("IDX_NAMED_TYPES_HIERARCHY", (namedTypeId, parentNamedTypeId))

    // Foreign keys
    def namedType: ForeignKeyQuery[NamedTypes, NamedType] =
      foreignKey("NAMED_TYPES_HIERARCHY_FK_NAMED_TYPES", namedTypeId, namedTypes)(_.id)

    def parentNamedType: ForeignKeyQuery[NamedTypes, NamedType] =
      foreignKey("NAMED_TYPES_HIERARCHY_PARENT_FK_NAMED_TYPES", parentNamedTypeId, namedTypes)(_.id)

    // *
    def * : ProvenShape[NamedTypeLink] =
      (namedTypeId, parentNamedTypeId) <> (NamedTypeLink.tupled, NamedTypeLink.unapply)

  }

  object namedTypeLinks extends TableQuery(new NamedTypeLinks(_))

  class NamedTypeProperties(tag: Tag) extends Table[NamedTypeProperty](tag, "NAMED_TYPES_PROPERTIES") {

    // Columns
    def namedTypeId: Rep[UUID] = column[UUID]("NAMED_TYPE_ID")

    def propertyKeyId: Rep[UUID] = column[UUID]("PROPERTY_KEY_ID")

    // Indexes
    def pk: PrimaryKey = primaryKey("IDX_NAMED_TYPES_PROPERTIES", (namedTypeId, propertyKeyId))

    // Foreign keys
    def namedType: ForeignKeyQuery[NamedTypes, NamedType] =
      foreignKey("NAMED_TYPES_PROPERTIES_FK_NAMED_TYPES", namedTypeId, namedTypes)(_.id)

    def propertyKey: ForeignKeyQuery[PropertyKeys, PropertyKey] =
      foreignKey("NAMED_TYPES_PROPERTIES_FK_PROPERTY_KEYS", propertyKeyId, propertyKeys)(_.id)

    // *
    def * : ProvenShape[NamedTypeProperty] =
      (namedTypeId, propertyKeyId) <> (NamedTypeProperty.tupled, NamedTypeProperty.unapply)

  }

  object namedTypeProperties extends TableQuery(new NamedTypeProperties(_))

  _schemas ++= Seq(
    namedTypes.schema,
    namedTypeLinks.schema,
    namedTypeProperties.schema
  )

}
