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

package ch.renku.acceptancetests.workflows

import ch.renku.acceptancetests.model.projects
import ch.renku.acceptancetests.model.projects.{ProjectDetails, Template, Visibility}
import ch.renku.acceptancetests.pages._
import ch.renku.acceptancetests.tooling.KnowledgeGraphModel.{Image, NewProject, ProjectTemplate}
import ch.renku.acceptancetests.tooling.TestLogger.logger
import ch.renku.acceptancetests.tooling.{AcceptanceSpec, KnowledgeGraphApi}
import org.openqa.selenium.interactions.Actions
import org.scalatest.Outcome

import java.lang.System.getProperty
import scala.concurrent.duration._

trait ExtantProject {

  lazy val maybeExtantProjectSlug: Option[projects.Slug] =
    (Option(getProperty("extant")) orElse sys.env.get("RENKU_TEST_EXTANT_PROJECT"))
      .map(_.trim)
      .map(projects.Slug)

  def maybeExtantProject(projectVisibility: Visibility): Option[ProjectDetails] =
    (Option(getProperty("extant")) orElse sys.env.get("RENKU_TEST_EXTANT_PROJECT"))
      .map(_.trim)
      .map { readMeTitle =>
        ProjectDetails(readMeTitle,
                       projectVisibility,
                       description = "unused",
                       template = Template("Not used"),
                       readmeTitle = readMeTitle
        )
      }
}

trait PrivateProject extends Project with ExtantProject with KnowledgeGraphApi {
  self: AcceptanceSpec =>

  protected override lazy val projectVisibility: Visibility = Visibility.Private
  protected implicit override lazy val projectDetails: ProjectDetails =
    maybeExtantProject(projectVisibility).getOrElse(ProjectDetails.generate().copy(visibility = projectVisibility))
}

trait Project extends RemoveProject with ExtantProject with KnowledgeGraphApi {
  self: AcceptanceSpec =>

  protected lazy val projectVisibility: Visibility = Visibility.Public

  protected implicit lazy val projectDetails: ProjectDetails =
    Option
      .when(docsScreenshots.captureScreenshots)(docsScreenshots.projectDetails)
      .orElse(maybeExtantProject(projectVisibility))
      .getOrElse(ProjectDetails.generate())

  protected implicit lazy val projectPage: ProjectPage = ProjectPage.createFrom(projectDetails)

  def `create or use extant project`: projects.Slug =
    maybeExtantProjectSlug match {
      case Some(slug) =>
        When(s"an extant project $slug is configured")
        slug
      case None =>
        val namespaceId = `find user namespace ids`.headOption.getOrElse(fail("No namespaces found"))
        val newProject  = NewProject.generate(namespaceId, ProjectTemplate.pythonMinimal, Image.wheelPngExample)
        val slug        = `POST /knowledge-graph/projects`(newProject)
        When(s"a $slug project is created")
        slug
    }

  def `create, continue or open a project`: Unit = maybeExtantProject(projectVisibility) match {
    case Some(_) => `open a project`
    case _       => `create or continue with a project`
  }

  private def `create or continue with a project`: Unit =
    if (`project exists in GitLab`(projectDetails.asProjectIdentifier)) `open a project`
    else `create a new project`

  private def `open a project`: Unit = {
    `try few times with page reload` { _ =>
      go to ProjectsPage sleep (2 seconds)
      verify browserAt ProjectsPage
    }

    And("they click on the 'Your Projects' tab")
    click on ProjectsPage.YourProjects.tab

    And("they enter project title in the search box")
    ProjectsPage.YourProjects.searchField.clear() sleep (1 second)
    ProjectsPage.YourProjects.searchField.enterValue(projectDetails.title)

    And("they click the Search button")
    click on ProjectsPage.YourProjects.searchButton sleep (1 second)

    Then(s"the '${projectDetails.title}'(${projectDetails.asProjectSlug}) project should be listed")
    val projectLink = ProjectsPage.YourProjects.linkTo(projectDetails)

    When("they click on the link")
    click on projectLink sleep (1 second)

    `try few times with page reload` { _ =>
      Then(s"they should see the '${projectDetails.title}' project page")
      verify browserAt projectPage
    }
  }

  private def `create a new project`: Unit = {

    if (DashboardPage.TopBar.mainNavToggler.isDisplayed)
      click on DashboardPage.TopBar.mainNavToggler

    When("user clicks on the 'New Project' menu item")
    click on DashboardPage.TopBar.plusDropdown
    click on DashboardPage.TopBar.projectOption sleep (5 seconds)

    `try few times with page reload` { _ =>
      Then("the New Project page gets displayed")
      verify browserAt NewProjectPage
    }

    `try few times with page reload` { _ =>
      When("user fills in and submits the new project details form")
      `fill in new project form and submit`
    }

    val projectPage = ProjectPage createFrom projectDetails

    `wait for project activation`(projectPage.asProjectIdentifier.asProjectSlug) match {
      case Left(err) =>
        logger.error(s"$err - retrying the creation process")
        `create a new project`
      case _ => ()
    }

    pause asLongAsBrowserAt NewProjectPage sleep (1 second)
    Then(
      s"the project '${projectDetails.title}'(${projectDetails.asProjectSlug}) gets created and the Project page gets displayed"
    )

    verify browserAt projectPage

    `try few times with page reload` { _ =>
      When("the user navigates to the Overview -> Description tab")
      click on projectPage.Overview.tab
      click on projectPage.Overview.overviewGeneralButton
    }

    Then("they should see project's README.md content")
    verify that projectPage.Overview.Description.title is "README.md"

    `try few times with page reload` { _ =>
      When("the user navigates to the Files tab")
      click on projectPage.Files.tab
    }

    And("they click on the README.md file in the File View")
    click on (projectPage.Files.FileView file "README.md") sleep (2 seconds)
    Then("they should see the file header")
    verify that projectPage.Files.Info.heading contains "README.md"
    And("the commit hash")
    verify that projectPage.Files.Info.commit matches "^Commit: [0-9a-f]{8}$"
    And("creator name and timestamp")
    verify that projectPage.Files.Info.creatorAndTime contains userCredentials.fullName
    And("the readme header which is the project title")
    verify that projectPage.Files.Info.title is projectDetails.readmeTitle
    And("the readme content")
    verify that projectPage.Files.Info.content contains "This is a Renku project"
  }

  private def `fill in new project form and submit`: Unit = eventually {

    When(s"enters '${projectDetails.title}' as the title")
    NewProjectPage.titleField.clear() sleep (5 seconds)
    NewProjectPage.titleField.enterValue(projectDetails.title)

    When("enters the description")
    NewProjectPage.descriptionField.clear() sleep (1 second)
    NewProjectPage.descriptionField.enterValue(projectDetails.description)

    When(s"selects the visibility '${projectDetails.visibility}'")
    NewProjectPage.visibilityRadioInput(projectDetails.visibility).click() sleep (1 second)

    scrollDown sleep (1 second)

    `try few times without page reload` { _ =>
      When(s"selects the '${projectDetails.template}' template")
      NewProjectPage.templateCard(projectDetails.template).click() sleep (10 seconds)
    }

    docsScreenshots.takeScreenshot()

    scrollDown sleep (1 second)
    // Move the mouse off the field to prevent the tooltip from blocking the button
    new Actions(webDriver)
      .moveByOffset(20, 20)
      .build()
      .perform()
    scrollDown sleep (1 second)

    And("clicks the 'Create' button")
    NewProjectPage.createButton.isEnabled shouldBe true
    NewProjectPage.createButton.click() sleep (2 seconds)

    `wait for project creation`(projectDetails.asProjectIdentifier)
  }

  protected override type FixtureParam = Unit

  override def withFixture(test: OneArgTest): Outcome = {

    val outcome = withFixture(test.toNoArgTest((): FixtureParam))

    if (outcome.isSucceeded && maybeExtantProject(projectVisibility).isEmpty) {
      `remove project in GitLab`(projectDetails.asProjectIdentifier)
    }

    outcome
  }
}
