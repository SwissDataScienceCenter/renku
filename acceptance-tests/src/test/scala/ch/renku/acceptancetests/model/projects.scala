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

package ch.renku.acceptancetests.model

import cats.syntax.all._
import ch.renku.acceptancetests.generators.Generators.Implicits._
import ch.renku.acceptancetests.generators.Generators._
import ch.renku.acceptancetests.model.AuthorizationToken.{OAuthAccessToken, PersonalAccessToken}
import ch.renku.acceptancetests.model.projects.ProjectDetails._
import ch.renku.acceptancetests.model.users.UserCredentials
import ch.renku.acceptancetests.tooling.multipart.PartValueEncoder
import io.circe.DecodingFailure.Reason
import io.circe.syntax._
import io.circe.{Decoder, DecodingFailure, Encoder}
import org.scalacheck.Gen

import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Random

object projects {

  final case class ProjectIdentifier(namespace: String, path: String) {
    lazy val asProjectSlug:     projects.Slug = projects.Slug(s"$namespace/$path")
    override lazy val toString: String        = s"$namespace/$path"
  }

  final case class ProjectDetails(
      title:       String,
      visibility:  Visibility,
      description: String,
      template:    Template,
      readmeTitle: String
  ) {
    def asProjectIdentifier(implicit userCredentials: UserCredentials): ProjectIdentifier =
      ProjectIdentifier(
        namespace = userCredentials.userNamespace,
        path = title.toPathSegment
      )

    def asProjectSlug(implicit userCredentials: UserCredentials): projects.Slug =
      asProjectIdentifier.asProjectSlug
  }

  final case class Slug(value: String) { override lazy val toString: String = value }

  final case class Name(value: String) { override lazy val toString: String = value }
  object Name {

    implicit val pvEncoder: PartValueEncoder[Name] = _.value

    def generate(): Name = Name(
      s"test ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"))} ${Random.nextInt(100)}"
    )
  }

  final case class NamespaceId(value: Int) { override lazy val toString: String = value.toString }
  object NamespaceId {
    implicit val pvEncoder: PartValueEncoder[NamespaceId] = _.value.toString
  }

  final case class Description(value: String) { override lazy val toString: String = value }
  object Description {
    def generate(): Description = nonEmptyStrings().map(Description(_)).generateOne

    implicit val pvEncoder:   PartValueEncoder[Description] = _.value
    implicit val jsonEncoder: Encoder[Description]          = Encoder.instance(_.value.asJson)
    implicit val jsonDecoder: Decoder[Description]          = Decoder.instance(_.as[String].map(Description(_)))
  }

  final case class Keyword(value: String) { override lazy val toString: String = value }
  object Keyword {
    val generator:  Gen[Keyword] = nonEmptyStrings().map(Keyword(_))
    def generate(): Keyword      = generator.generateOne

    implicit val pvEncoder:   PartValueEncoder[Keyword] = _.value
    implicit val jsonEncoder: Encoder[Keyword]          = Encoder.instance(_.value.asJson)
    implicit val jsonDecoder: Decoder[Keyword]          = Decoder.instance(_.as[String].map(Keyword(_)))
  }

  final case class TemplateRepoUrl(value: String) { override lazy val toString: String = value }
  object TemplateRepoUrl {
    implicit val pvEncoder: PartValueEncoder[TemplateRepoUrl] = _.value
  }

  final case class TemplateId(value: String) { override lazy val toString: String = value }
  object TemplateId {
    implicit val pvEncoder: PartValueEncoder[TemplateId] = _.value
  }

  final case class Template(name: String) { override lazy val toString: String = name }

  sealed abstract class Visibility(val value: String)
  object Visibility {
    case object Public   extends Visibility(value = "public")
    case object Private  extends Visibility(value = "private")
    case object Internal extends Visibility(value = "internal")

    val all: Set[Visibility] = Set(Public, Internal, Private)

    def generate(): Visibility = Gen.oneOf(all).generateOne

    implicit val pvEncoder:   PartValueEncoder[Visibility] = _.value
    implicit val jsonEncoder: Encoder[Visibility]          = Encoder.instance(_.value.asJson)
    implicit val jsonDecoder: Decoder[Visibility] = Decoder.instance { cur =>
      cur.as[String].flatMap { v =>
        all
          .find(_.value == v)
          .fold(
            ifEmpty = DecodingFailure(Reason.CustomReason(s"'$v' is not a valid Visibility"), cur).asLeft[Visibility]
          )(_.asRight)
      }
    }
  }

  final case class ProjectUrl(value: String) { override lazy val toString: String = value }
  object ProjectUrl {

    implicit class ProjectUrlOps(projectUrl: ProjectUrl)(implicit userCredentials: UserCredentials) {

      def add(authorizationToken: AuthorizationToken): String = {
        val protocol = URI.create(projectUrl.value).toURL.getProtocol
        projectUrl.value
          .replace(
            s"$protocol://",
            s"$protocol://${tokenPart(authorizationToken)}"
          )
      }

      private lazy val tokenPart: AuthorizationToken => String = {
        case OAuthAccessToken(token)    => s"oauth2:$token@"
        case PersonalAccessToken(token) => s"gitlab-ci-token:$token@"
      }
    }
  }

  object ProjectDetails {

    def generate(maybeTitle:       Option[String] = None,
                 visibility:       Visibility = Visibility.Public,
                 maybeDescription: Option[String] = None,
                 maybeTemplate:    Option[String] = None
    ): ProjectDetails = {
      val title = maybeTitle.getOrElse(Name.generate().value)
      val desc = maybeDescription.getOrElse(
        prefixParagraph("Generated by tests: ", maxWords = 3).generateOne
      )
      val template = Template(maybeTemplate getOrElse "Python")
      ProjectDetails(title, visibility, desc, template, readmeTitle = title)
    }

    implicit class TitleOps(title: String) {
      lazy val toPathSegment: String = title.replace(" ", "-")
    }
  }
}
