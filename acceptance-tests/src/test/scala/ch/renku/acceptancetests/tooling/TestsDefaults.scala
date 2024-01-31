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

final case class TestsDefaults(env:               Option[String],
                               gitlaburl:         Option[String] = None,
                               cliversion:        Option[String] = None,
                               email:             Option[String] = None,
                               username:          Option[String] = None,
                               password:          Option[String] = None,
                               fullname:          Option[String] = None,
                               gitlabaccesstoken: Option[String] = None
)

object TestsDefaults {
  import pureconfig.ConfigSource
  import pureconfig.generic.auto._

  import java.nio.file.Paths

  private val defaultConfigFileName = "./tests-defaults.conf"

  def apply(): TestsDefaults =
    if (Paths.get(defaultConfigFileName).toFile.exists())
      ConfigSource
        .file(defaultConfigFileName)
        .load[TestsDefaults]
        .fold(error => throw new Exception(error.prettyPrint()), identity)
    else
      TestsDefaults(
        env = Some("https://dev.renku.ch")
      )
}
