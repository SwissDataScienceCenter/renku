/*
 * Copyright 2022 Swiss Data Science Center (SDSC)
 * A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
 * Eidgenössische Technische Hochschule Zürich (ETHZ).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.renku.acceptancetests.tooling

import TestLogger._
import cats.effect.IO
import cats.syntax.all._
import ch.renku.acceptancetests.model.projects.ProjectIdentifier
import io.circe.literal.JsonStringContext
import io.circe.{Json, JsonObject}
import org.http4s.Status.{NotFound, Ok}
import org.http4s.circe._
import org.scalatest.Assertions.fail

import scala.annotation.tailrec
import scala.concurrent.duration._

trait KnowledgeGraphApi extends RestClient {
  self: AcceptanceSpecData with GitLabApi with Grammar with IOSpec =>

  def `wait for KG to process events`(projectId: ProjectIdentifier): Unit = {
    sleep(1 second)
    val gitLabProjectId = `get GitLab project id`(projectId)
    checkStatusAndWait(projectId, gitLabProjectId)
  }

  def findLineage(projectPath: String, filePath: String): JsonObject = {
    val query = s"""
                   |{ 
                   |  lineage(projectPath: "$projectPath", filePath: "$filePath") {
                   |    nodes { id location label type } 
                   |    edges { source target } 
                   |  } 
                   |}
                   |""".stripMargin

    val body = json"""{ "query": $query }"""
    POST(renkuBaseUrl / "api" / "kg" / "graphql")
      .withEntity(body)
      .send(whenReceived(status = Ok) >=> bodyToJson)
      .extract(jsonRoot.data.lineage.obj.getOption)
      .getOrElse(fail(s"Cannot find lineage data for project $projectPath file $filePath"))
  }

  @tailrec
  private def checkStatusAndWait(projectId: ProjectIdentifier, gitLabProjectId: Int, attempt: Int = 1): Unit =
    if (attempt == 60 * 5) // 5 minutes
      fail(s"Events for '$projectId' project not processed after 5 minutes")
    else if (findTotalDone(projectId, gitLabProjectId) == 0) {
      sleep(1 second)
      checkStatusAndWait(projectId, gitLabProjectId)
    } else if (findProgress(projectId, gitLabProjectId) != 100d) {
      sleep(1 second)
      checkStatusAndWait(projectId, gitLabProjectId)
    }

  private def findProgress(projectId: ProjectIdentifier, gitLabProjectId: Int): Double =
    GET(renkuBaseUrl / "api" / "projects" / gitLabProjectId.toString / "graph" / "status")
      .send { response =>
        response.status match {
          case Ok       => response.as[Json].map(jsonRoot.progress.double.getOption)
          case NotFound => 0d.some.pure[IO]
          case other =>
            logger.warn(s"Finding processing status for '$projectId' returned $other")
            0d.some.pure[IO]
        }
      }
      .getOrElse(0d)

  private def findTotalDone(projectId: ProjectIdentifier, gitLabProjectId: Int): Int =
    GET(renkuBaseUrl / "api" / "projects" / gitLabProjectId.toString / "graph" / "status")
      .send { response =>
        response.status match {
          case Ok       => response.as[Json].map(jsonRoot.total.int.getOption)
          case NotFound => 0.some.pure[IO]
          case other =>
            logger.warn(s"Finding processing status for '$projectId' returned $other")
            0.some.pure[IO]
        }
      }
      .getOrElse(0)
}
