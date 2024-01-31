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

package ch.renku.acceptancetests.tooling.multipart

import cats.effect.IO
import cats.syntax.all._
import cats.{Functor, MonadThrow}
import org.http4s.multipart.{Multipart, Part}

object syntax {

  final implicit class EncoderOps[A](private val value: A) extends AnyVal {

    def asPart(partName: String)(implicit encoder: PartEncoder[IO, A]): Part[IO] =
      encoder(partName, value)
  }

  final implicit class EncoderFOps[M[_]: Functor, A](private val value: M[A]) {

    def asParts(partName: String)(implicit encoder: PartEncoder[IO, A]): M[Part[IO]] =
      value.map(_.asPart(s"$partName$partNameSuffix"))

    private lazy val partNameSuffix: String = value match {
      case _: Iterable[_] => "[]"
      case _ => ""
    }
  }

  final implicit class MultipartOps[F[_]: MonadThrow](private val multipart: Multipart[F]) {

    def part(partName: String): F[Part[F]] =
      findPart(partName).fold(new Exception(s"No '$partName' in the request").raiseError[F, Part[F]])(_.pure[F])

    def findPart(partName: String): Option[Part[F]] =
      multipart.parts.find(_.name contains partName)

    def filterParts(partName: String): List[Part[F]] =
      multipart.parts.filter(_.name exists (_ startsWith partName)).toList
  }

  implicit def fromPartValueEncoder[F[_], A](implicit pvEnc: PartValueEncoder[A]): PartEncoder[F, A] =
    PartEncoder.instance[F, A]((partName: String, a: A) => Part.formData[F](partName, pvEnc(a)))
}
