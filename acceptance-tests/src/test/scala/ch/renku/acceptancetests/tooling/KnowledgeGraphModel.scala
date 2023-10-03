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

import cats.effect.IO
import cats.syntax.all._
import ch.renku.acceptancetests.generators.Generators.Implicits._
import ch.renku.acceptancetests.model
import ch.renku.acceptancetests.model.{images, projects}
import ch.renku.acceptancetests.tooling.multipart.PartEncoder
import fs2.io.file.Path
import io.circe.Decoder._
import io.circe.{Decoder, Encoder, Json}
import org.http4s.headers.{`Content-Disposition`, `Content-Type`}
import org.http4s.multipart.{Multipart, Multiparts, Part}
import org.http4s.{Headers, MediaType}
import org.typelevel.ci._

object KnowledgeGraphModel {

  final case class KGProjectDetails(maybeDescription: Option[projects.Description],
                                    keywords:         Set[projects.Keyword],
                                    visibility:       projects.Visibility,
                                    images:           List[model.images.ImageUri]
  )

  object KGProjectDetails {
    private implicit val imageDecoder: Decoder[images.ImageUri] =
      _.downField("location").as[String].map(images.ImageUri(_))

    implicit val jsonDecoder: Decoder[KGProjectDetails] = { cur =>
      (
        cur.downField("description").as[Option[projects.Description]],
        cur.downField("keywords").as[Set[projects.Keyword]],
        cur.downField("visibility").as[projects.Visibility],
        cur.downField("images").as[List[images.ImageUri]](decodeList(imageDecoder)),
      ).mapN(KGProjectDetails.apply)
    }
  }

  final case class NewProject(name:             projects.Name,
                              namespaceId:      projects.NamespaceId,
                              maybeDescription: Option[projects.Description],
                              keywords:         Set[projects.Keyword],
                              visibility:       projects.Visibility,
                              template:         ProjectTemplate,
                              maybeImage:       Option[Image]
  )

  object NewProject {

    def generate(namespaceId: projects.NamespaceId, template: ProjectTemplate, image: Image): NewProject = {
      val name = projects.Name.generate()
      NewProject(
        name,
        namespaceId,
        projects.Description(s"Description for '$name'").some,
        projects.Keyword.generator.generateList().toSet,
        projects.Visibility.generate(),
        template,
        image.some
      )
    }

    object MultipartEncoder {

      def encode(newProject: NewProject): IO[Multipart[IO]] = {
        import multipart.syntax._

        val parts =
          Vector(
            newProject.maybeDescription.asParts("description"),
            newProject.maybeImage.asParts("image")
          ).flatten
            .appended(newProject.name.asPart("name")(fromPartValueEncoder(projects.Name.pvEncoder)))
            .appended(newProject.namespaceId.asPart("namespaceId"))
            .appended(newProject.visibility.asPart("visibility"))
            .appended(newProject.template.repoUrl.asPart("templateRepositoryUrl"))
            .appended(newProject.template.id.asPart("templateId"))
            .appendedAll(newProject.keywords.toList.asParts("keywords"))

        Multiparts.forSync[IO].flatMap(_.multipart(parts))
      }
    }
  }

  final case class Image(path: Path, mediaType: MediaType) {
    def toName: images.Name = images.Name(path.fileName.toString)
  }

  object Image {

    implicit val partEncoder: PartEncoder[IO, Image] =
      PartEncoder.instance { case (partName, Image(path, mediaType)) =>
        Part.fileData(partName, path, `Content-Type`(mediaType))
      }

    val wheelPngExample: Image =
      Image(Path(getClass.getClassLoader.getResource("wheel.png").getPath), MediaType.image.png)
    val bikeJpgExample: Image =
      Image(Path(getClass.getClassLoader.getResource("bike.jpeg").getPath), MediaType.image.jpeg)
  }

  final case class ProjectTemplate(repoUrl: projects.TemplateRepoUrl, id: projects.TemplateId)

  object ProjectTemplate {
    val pythonMinimal: ProjectTemplate =
      ProjectTemplate(projects.TemplateRepoUrl("https://github.com/SwissDataScienceCenter/renku-project-template"),
                      projects.TemplateId("python-minimal")
      )
  }

  final case class ProjectUpdates(newDescription: Option[Option[projects.Description]],
                                  newKeywords:    Option[Set[projects.Keyword]],
                                  newVisibility:  Option[projects.Visibility],
                                  newImage:       Option[Option[Image]]
  )

  object ProjectUpdates {

    implicit val jsonEncoder: Encoder[ProjectUpdates] = Encoder.instance {
      case ProjectUpdates(newDescription, newKeywords, newVisibility, None) =>
        import io.circe.syntax._

        Json.obj(
          List(
            newDescription.map(v => "description" -> v.fold(Json.Null)(_.asJson)),
            newKeywords.map(v => "keywords" -> v.asJson),
            newVisibility.map(v => "visibility" -> v.asJson)
          ).flatten: _*
        )
      case ProjectUpdates(_, _, _, Some(_)) =>
        throw new Exception("Cannot encode to JSON ProjectUpdates with image")
    }

    object MultipartEncoder {

      def encode(updates: ProjectUpdates): IO[Multipart[IO]] = {
        import multipart.syntax._

        val parts =
          Vector(
            updates.newVisibility.map(_.asPart("visibility")),
            updates.newDescription.map(_.fold("")(_.value).asPart("description")),
            updates.newImage.map(
              _.fold(
                Part[IO](
                  Headers(`Content-Disposition`("form-data", Map(ci"name" -> "image")),
                          `Content-Type`(MediaType.image.jpeg)
                  ),
                  fs2.Stream.empty
                )
              )(_.asPart("image"))
            )
          ).flatten.appendedAll(updates.newKeywords.map(_.toList.asParts("keywords")).getOrElse(List.empty))

        Multiparts.forSync[IO].flatMap(_.multipart(parts))
      }
    }
  }
}
