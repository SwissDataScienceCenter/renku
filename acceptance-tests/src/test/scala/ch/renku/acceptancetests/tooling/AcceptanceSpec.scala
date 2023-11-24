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

import org.scalatest._
import org.scalatest.featurespec.FixtureAnyFeatureSpec
import org.scalatest.matchers.should
import org.scalatestplus.selenium.WebBrowser

trait AcceptanceSpec
    extends FixtureAnyFeatureSpec
    with BddWording
    with BeforeAndAfterAll
    with should.Matchers
    with WebBrowser
    with Grammar
    with GitLabApi
    with RenkuApi
    with AcceptanceSpecData
    with AcceptanceSpecPatience
    with WebDriveredSpec
    with IOSpec {

  protected implicit val browser: AcceptanceSpec = this

  protected override type FixtureParam = Unit

  override def withFixture(test: OneArgTest): Outcome =
    withFixture(test.toNoArgTest((): FixtureParam))
}
