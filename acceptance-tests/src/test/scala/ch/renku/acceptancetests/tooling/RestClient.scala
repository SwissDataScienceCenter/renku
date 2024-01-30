/*
 * Copyright 2024 Swiss Data Science Center (SDSC)
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

import cats.Applicative
import cats.effect.IO._
import cats.effect.unsafe.IORuntime
import cats.effect.{IO, Temporal}
import cats.syntax.all._
import ch.renku.acceptancetests.model.{AuthorizationToken, BaseUrl}
import ch.renku.acceptancetests.tooling.TestLogger._
import io.circe.optics.JsonPath
import io.circe.optics.JsonPath.root
import io.circe.{Decoder, Json}
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.pipeline.Command
import org.http4s.circe.CirceEntityCodec._
import org.http4s.circe._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.client.{Client, ConnectionFailure}
import org.openqa.selenium.WebDriver
import org.scalatest.Assertions.fail

import java.net.ConnectException
import scala.concurrent.duration._
import scala.jdk.CollectionConverters._
import scala.util.control.NonFatal

trait RestClient extends Http4sClientDsl[IO] {

  implicit val ioRuntime: IORuntime

  val jsonRoot: JsonPath = root

  private lazy val clientBuilder = BlazeClientBuilder[IO]

  def GET(baseUrl: BaseUrl): Request[IO] = Request[IO](
    Method.GET,
    uri = Uri.fromString(baseUrl.toString).fold(error => fail(error.getMessage()), identity)
  )

  def POST(baseUrl: BaseUrl): Request[IO] = Request[IO](
    Method.POST,
    Uri.fromString(baseUrl.toString).fold(error => fail(error.getMessage()), identity)
  )

  def PATCH(baseUrl: BaseUrl): Request[IO] = Request[IO](
    Method.PATCH,
    Uri.fromString(baseUrl.toString).fold(error => fail(error.getMessage()), identity)
  )

  def DELETE(baseUrl: BaseUrl): Request[IO] = Request[IO](
    Method.DELETE,
    uri = Uri.fromString(baseUrl.toString).fold(error => fail(error.getMessage()), identity)
  )

  implicit class RequestOps(request: Request[IO]) {

    def addCookiesFrom(webDriver: WebDriver): Request[IO] =
      webDriver
        .manage()
        .getCookies
        .asScala
        .foldLeft(request)((r, c) => r.addCookie(c.getName, c.getValue))

    def withAuthorizationToken(authorizationToken: AuthorizationToken): Request[IO] =
      request.putHeaders(Headers(authorizationToken.asHeader))

    def send[A](processResponse: Response[IO] => IO[A]): A =
      sendIO(processResponse).unsafeRunSync()

    def sendIO[A](processResponse: Response[IO] => IO[A]): IO[A] =
      clientBuilder.resource.use(sendRequest(processResponse))

    private def sendRequest[A](processResponse: Response[IO] => IO[A])(client: Client[IO]): IO[A] =
      client
        .run(request)
        .use(response =>
          processResponse(response).recoverWith(retryOnConnectionProblem(client, request)(processResponse))
        )

    private def retryOnConnectionProblem[A](client: Client[IO], request: Request[IO])(
        processResponse: Response[IO] => IO[A]
    ): PartialFunction[Throwable, IO[A]] = { case NonFatal(cause) =>
      cause match {
        case e @ (_: ConnectionFailure | _: ConnectException | _: Command.EOF.type | _: InvalidBodyException) =>
          for {
            _      <- logger.warn(s"Connectivity problem to ${request.method} ${request.uri}. Retrying...", e).pure[IO]
            _      <- Temporal[IO].sleep(1 second)
            result <- sendRequest(processResponse)(client)
          } yield result
        case other => fail(s"${request.method} ${request.uri} ${other.getMessage}")
      }
    }
  }

  def whenReceived(status: Status): Response[IO] => IO[Response[IO]] = { response =>
    if (response.status == status) response.pure[IO]
    else
      new Exception(s"returned ${response.status} which is not expected $status.").raiseError
  }

  def bodyToJson: Response[IO] => IO[Json] = _.as[Json]

  def decodePayload[A](implicit decoder: Decoder[A]): Response[IO] => IO[A] = _.as[A]

  def expect(status: Status, otherwiseLog: String): Response[IO] => IO[Unit] = { response =>
    Applicative[IO].whenA(response.status != status) {
      response.as[Json] >>= { body =>
        new Exception(
          s"returned ${response.status} which is not expected $status. $otherwiseLog\n${body.spaces2}"
        ).raiseError
      }
    }
  }

  def mapResponse[A](f: PartialFunction[Response[IO], A]): Response[IO] => IO[A] = response => f(response).pure[IO]

  def mapResponseIO[A](f: PartialFunction[Response[IO], IO[A]]): Response[IO] => IO[A] = f

  implicit class JsonOps(json: Json) {
    def extract[V](extractor: Json => V): V = extractor(json)
  }
}
