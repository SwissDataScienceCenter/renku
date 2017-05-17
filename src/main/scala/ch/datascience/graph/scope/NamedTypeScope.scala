package ch.datascience.graph.scope

import ch.datascience.graph.scope.persistence.PersistedNamedTypes
import ch.datascience.graph.types.{NamedType, PropertyKey}

import scala.collection.concurrent
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by johann on 16/05/17.
  */
trait NamedTypeScope[TypeKey, PropKey] { this: PropertyScope[PropKey] =>

  final def getDefinitionsFor(key: TypeKey)(implicit ec: ExecutionContext): Future[(Map[PropKey, PropertyKey[PropKey]], Map[TypeKey, NamedType[TypeKey, PropKey]])] = {
    getNamedTypeFor(key) flatMap {
      case Some(namedType) =>
        for {
          superTypesDefinitions <- getNamedTypesFor(namedType.superTypes)
          propertiesDefinition <- getPropertiesFor(namedType.properties)
        } yield (propertiesDefinition, superTypesDefinitions + (namedType.key -> namedType))
      case None => Future.successful((Map.empty, Map.empty))
    }
  }

  final def getDefinitionsFor(keys: Set[TypeKey])(implicit ec: ExecutionContext): Future[(Map[PropKey, PropertyKey[PropKey]], Map[TypeKey, NamedType[TypeKey, PropKey]])] = {
    for {
      namedTypesDefinitions <- getNamedTypesFor(keys)
      allSuperTypes = namedTypesDefinitions.values.map(_.superTypes).reduce(_ ++ _)
      allProperties = namedTypesDefinitions.values.map(_.properties).reduce(_ ++ _)
      superTypesDefinitions <- getNamedTypesFor(allSuperTypes)
      propertiesDefinition <- getPropertiesFor(allProperties)
    } yield (propertiesDefinition, namedTypesDefinitions ++ superTypesDefinitions)
  }

  final def getNamedTypeFor(key: TypeKey)(implicit ec: ExecutionContext): Future[Option[NamedType[TypeKey, PropKey]]] = {
    namedTypeDefinitions get key match {
      case Some(namedType) => Future.successful( Some(namedType) )
      case None => {
        val result = persistedNamedTypes.fetchNamedTypeFor(key)

        // If we get a property key, then we add it to our scope
        result.onSuccess({
          case Some(namedType) => namedTypeDefinitions.put(namedType.key, namedType)
        })(ec)

        result
      }
    }
  }

  final def getNamedTypesFor(keys: Set[TypeKey])(implicit ec: ExecutionContext): Future[Map[TypeKey, NamedType[TypeKey, PropKey]]] = {
    // Locally, sort known keys and unkown keys
    val tryLocally = (for (key <- keys) yield key -> namedTypeDefinitions.get(key)).toMap
    val knownNamedTypes: Map[TypeKey, NamedType[TypeKey, PropKey]] = for {
      (key, opt) <- tryLocally
      namedType <- opt
    } yield key -> namedType
    val unknownKeys: Set[TypeKey] = for {
      key <- tryLocally.keySet
      if tryLocally(key).isEmpty
    } yield key

    // Resolve unknown keys
    val resolved = persistedNamedTypes.fetchNamedTypesFor(unknownKeys)

    // Update resolved keys
    resolved.map({ definitions =>
      for (namedType <- definitions.values) {
        namedTypeDefinitions.put(namedType.key, namedType)
      }
    })(ec)

    resolved.map({ definitions =>
      knownNamedTypes ++ definitions
    })(ec)
  }

  final def getCachedPropertiesAndNamedTypes: Future[(collection.Map[PropKey, PropertyKey[PropKey]], collection.Map[TypeKey, NamedType[TypeKey, PropKey]])] = synchronized {
    Future.successful( (propertyDefinitions.readOnlySnapshot(), namedTypeDefinitions.readOnlySnapshot()) )
  }

  final def getCachedNamedTypes: Future[collection.Map[TypeKey, NamedType[TypeKey, PropKey]]] = {
    Future.successful( namedTypeDefinitions.readOnlySnapshot() )
  }

  protected def namedTypeDefinitions: concurrent.TrieMap[TypeKey, NamedType[TypeKey, PropKey]]

  protected def persistedNamedTypes: PersistedNamedTypes[TypeKey, PropKey]

}
