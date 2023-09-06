/*
 * Copyright 2023 Swiss Data Science Center (SDSC)
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

import ch.renku.acceptancetests.model.projects
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}

object KnowledgeGraphModel {

  final case class KGProjectDetails(description: Option[String], keywords: Set[String], visibility: projects.Visibility)

  object KGProjectDetails {

    implicit val jsonDecoder: Decoder[KGProjectDetails] =
      Decoder.forProduct3("description", "keywords", "visibility")(KGProjectDetails.apply)
  }

  final case class ProjectUpdates(newDescription: Option[Option[String]],
                                  newKeywords:    Option[Set[String]],
                                  newVisibility:  Option[projects.Visibility]
  )

  object ProjectUpdates {

    implicit val jsonEncoder: Encoder[ProjectUpdates] = Encoder.instance {
      case ProjectUpdates(newDescription, newKeywords, newVisibility) =>
        Json.obj(
          List(
            newDescription.map(v => "description" -> v.fold(Json.Null)(_.asJson)),
            newKeywords.map(v => "keywords" -> v.asJson),
            newVisibility.map(v => "visibility" -> v.asJson)
          ).flatten: _*
        )
    }
  }
}
