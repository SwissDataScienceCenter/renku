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

package ch.renku.acceptancetests

import ch.renku.acceptancetests.tooling.console._
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, KnowledgeGraphApi, console}
import ch.renku.acceptancetests.workflows._
import io.circe.{Json, JsonObject}

import java.nio.file.Path

class LineageSpec
    extends AcceptanceSpec
    with Settings
    with Login
    with Project
    with CLIConfiguration
    with KnowledgeGraphApi {

  scenario("User can see lineage") {

    `log in to Renku`

    `setup git configuration`

    `setup renku CLI`

    `create, continue or open a project`

    val (scriptPath, outputFilePath) = `setup a workflow`

    When("all the events are processed by the knowledge-graph")
    `wait for KG to process events`(projectDetails.asProjectIdentifier, webDriver)

    `view the lineage of the outputFile`(outputFilePath)

    `verify the kg api returned the correct lineage data`(scriptPath, outputFilePath)

    `log out of Renku`
  }

  private def `setup a workflow` = {
    When("the user sets up a workflow")

    val projectUrl = `find project Http URL in the Overview Page`
    val tempFolder = createTempFolder
    implicit val workFolder: Path = tempFolder / projectPage.projectSlug

    val scriptPath     = "src/script.py"
    val outputFilePath = "data/output.txt"

    console.%>(c"git clone ${projectUrl add authorizationToken}")(tempFolder, userCredentials, ioRuntime)
    console %> c"renku migrate"

    console %> c"mkdir src"

    val scriptContent =
      s"""|f = open("$outputFilePath", "a")
          |f.write("Now the file has more content!")
          |f.close()""".stripMargin

    createFile(scriptContent, scriptPath)

    console %> c"chmod +x $scriptPath"
    console %> c"git add ."
    console %> c"git commit -am 'Created script'"

    And("the user runs it")

    console %> c"git lfs install --local"

    console %> c"renku run python $scriptPath"

    And("pushes the changes")

    console %> c"git push"
    (scriptPath, outputFilePath)
  }

  private def `view the lineage of the outputFile`(outputFilePath: String): Unit = {
    Then("the user can see the lineage")

    click on projectPage.Files.tab

    val dataFolder = outputFilePath.split("/").head

    reload whenUserCannotSee (projectPage.Files.FileView.folder(dataFolder)(_))

    click on projectPage.Files.FileView.folder(dataFolder)

    click on projectPage.Files.FileView.file(outputFilePath)

    click on projectPage.Files.FileView.lineageTab

    reload whenUserCannotSee (projectPage.Files.FileView.lineage(_))

    verify userCanSee projectPage.Files.FileView.lineage
  }

  private def `verify the kg api returned the correct lineage data`(scriptPath: String, outputFilePath: String) = {
    And("The KG API returns the lineage")

    val results = findLineage(projectDetails.asProjectSlug, outputFilePath).toMap
    val edges =
      results("edges").as[List[Json]].fold(_ => throw new Exception("Edges not found in Lineage response"), identity)
    val nodes =
      results("nodes")
        .as[List[JsonObject]]
        .fold(_ => throw new Exception("Nodes not found in Lineage response"), identity)

    edges.length shouldBe 2
    nodes.length shouldBe 3

    nodes.flatMap(_.toMap("location").asString) should contain allElementsOf List(scriptPath, outputFilePath)
  }

  private def createFile(withFileContent: String, withFileName: String)(implicit workFolder: Path): Unit = {
    import java.io._
    val pw = new PrintWriter(new File((workFolder / withFileName).toString))
    pw.write(withFileContent)
    pw.close()
  }
}
