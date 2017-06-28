package ch.datascience.graph.scope

import ch.datascience.graph.scope.persistence.PersistedNamedTypes
import ch.datascience.graph.types.{NamedType, PropertyKey}

import scala.collection.concurrent
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 16/05/17.
  */
trait NamedTypeScope { this: PropertyScope =>

  final def getDefinitionsFor(typeId: NamedType#TypeId)(implicit ec: ExecutionContext): Future[(Map[PropertyKey#Key, PropertyKey], Map[NamedType#TypeId, NamedType])] = {
    getNamedTypeFor(typeId) flatMap {
      case Some(namedType) =>
        for {
          superTypesDefinitions <- getNamedTypesFor(namedType.superTypes)
          propertiesDefinition <- getPropertiesFor(namedType.properties)
        } yield (propertiesDefinition, superTypesDefinitions + (namedType.typeId -> namedType))
      case None => Future.successful((Map.empty, Map.empty))
    }
  }

  final def getDefinitionsFor(typeIds: Set[NamedType#TypeId])(implicit ec: ExecutionContext): Future[(Map[PropertyKey#Key, PropertyKey], Map[NamedType#TypeId, NamedType])] = {
    for {
      namedTypesDefinitions <- getNamedTypesFor(typeIds)
      allSuperTypes = namedTypesDefinitions.values.map(_.superTypes).reduce(_ ++ _)
      allProperties = namedTypesDefinitions.values.map(_.properties).reduce(_ ++ _)
      superTypesDefinitions <- getNamedTypesFor(allSuperTypes)
      propertiesDefinition <- getPropertiesFor(allProperties)
    } yield (propertiesDefinition, namedTypesDefinitions ++ superTypesDefinitions)
  }

  final def getNamedTypeFor(typeId: NamedType#TypeId)(implicit ec: ExecutionContext): Future[Option[NamedType]] = {
    namedTypeDefinitions get typeId match {
      case Some(namedType) => Future.successful( Some(namedType) )
      case None => {
        val result = persistedNamedTypes.fetchNamedTypeFor(typeId)

        // If we get a property key, then we add it to our scope
        result.onSuccess({
          case Some(namedType) => namedTypeDefinitions.put(namedType.typeId, namedType)
        })(ec)

        result
      }
    }
  }

  final def getNamedTypesFor(typeIds: Set[NamedType#TypeId])(implicit ec: ExecutionContext): Future[Map[NamedType#TypeId, NamedType]] = {
    // Locally, sort known keys and unkown keys
    val tryLocally = (for (typeId <- typeIds) yield typeId -> namedTypeDefinitions.get(typeId)).toMap
    val knownNamedTypes: Map[NamedType#TypeId, NamedType] = for {
      (key, opt) <- tryLocally
      namedType <- opt
    } yield key -> namedType
    val unknownKeys: Set[NamedType#TypeId] = for {
      key <- tryLocally.keySet
      if tryLocally(key).isEmpty
    } yield key

    // Resolve unknown keys
    val resolved = persistedNamedTypes.fetchNamedTypesFor(unknownKeys)

    // Update resolved keys
    resolved.map({ definitions =>
      for (namedType <- definitions.values) {
        namedTypeDefinitions.put(namedType.typeId, namedType)
      }
    })(ec)

    resolved.map({ definitions =>
      knownNamedTypes ++ definitions
    })(ec)
  }

  final def getCachedPropertiesAndNamedTypes: Future[(collection.Map[PropertyKey#Key, PropertyKey], collection.Map[NamedType#TypeId, NamedType])] = synchronized {
    Future.successful( (propertyDefinitions.readOnlySnapshot(), namedTypeDefinitions.readOnlySnapshot()) )
  }

  final def getCachedNamedTypes: Future[collection.Map[NamedType#TypeId, NamedType]] = {
    Future.successful( namedTypeDefinitions.readOnlySnapshot() )
  }

  protected def namedTypeDefinitions: concurrent.TrieMap[NamedType#TypeId, NamedType]

  protected def persistedNamedTypes: PersistedNamedTypes

}
