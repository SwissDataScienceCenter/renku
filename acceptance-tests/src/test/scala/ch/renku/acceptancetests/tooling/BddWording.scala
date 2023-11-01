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

import org.scalatest.featurespec.FixtureAnyFeatureSpecLike
import org.scalatest.{Suite, Tag}

trait BddWording extends FixtureAnyFeatureSpecLike {
  self: Suite =>

  import TestLogger.logger

  override def Scenario(test: String, testTags: Tag*): ResultOfScenarioInvocation = {
    logger.info(test)
    super.Scenario(s"$test - ☑ success", testTags: _*)
  }

  def scenario(test: String, testTags: Tag*)(testFun: => Any): Unit =
    Scenario(test, testTags: _*)(_ => testFun)

  def ignore(test: String, testTags: Tag*)(testFun: => Any): Unit =
    super.ignore(test, testTags: _*)(_ => testFun)

  def Given(string: String): Unit = logger.info(s"Given $string")
  def When(string:  String): Unit = logger.info(s"When $string")
  def Then(string:  String): Unit = logger.info(s"Then $string")
  def And(string:   String): Unit = logger.info(s"And $string")
}
