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

package ch.renku.acceptancetests.model

import ch.renku.acceptancetests.generators.Generators.Implicits._
import ch.renku.acceptancetests.generators.Generators.httpUrls
import org.scalacheck.Gen

object images {

  final case class Name(value: String) { override lazy val toString: String = value }

  final case class ImageUri(value: String) {
    override lazy val toString: String = value
    lazy val toName:            Name   = Name(value.split("/").last)
  }
  object ImageUri {
    val generator:  Gen[ImageUri] = httpUrls.map(ImageUri(_))
    def generate(): ImageUri      = generator.generateOne
  }
}
