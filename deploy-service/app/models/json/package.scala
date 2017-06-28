package models

import ch.datascience.graph.elements.mutation.Mutation
import ch.datascience.graph.elements.mutation.json.MutationFormat
import play.api.libs.json._

/**
  * Created by johann on 25/04/17.
  */
package object json {

  implicit lazy val deployRequestReads: Reads[DeployRequest] = DeployRequestMappers.deployRequestReads
  implicit lazy val deployResultWrite: Writes[DeployResult] = DeployResultMappers.deployResultWrite
  implicit lazy val mutationFormat: Format[Mutation] = MutationFormat
}
