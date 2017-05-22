package ch.datascience.graph.elements.mutation.impl

import ch.datascience.graph.elements.Properties
import ch.datascience.graph.elements.persistence.{NewEdge, NewVertex}

/**
  * Created by jeberle on 15.05.17.
  */
case class ImplNewEdge[
  +Id,
  Key,
  +Value
](
   tempId: NewEdge[Nothing, Nothing, Nothing, Nothing]#TempId,
   from: Either[Id, NewVertex[Nothing, Nothing, Nothing, Nothing, Nothing, Nothing]#TempId],
   to: Either[Id, NewVertex[Nothing, Nothing, Nothing, Nothing, Nothing, Nothing]#TempId],
   properties: Properties[Key, Value, ImplNewRecordProperty[Key, Value]]
 ) extends NewEdge[Id, Key, Value, ImplNewRecordProperty[Key, Value]]
