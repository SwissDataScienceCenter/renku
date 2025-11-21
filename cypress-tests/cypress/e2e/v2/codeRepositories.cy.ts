import {
  getRandomString,
  getUserData,
  validateLoginV2,
} from "../../support/commands/general";
import { ProjectIdentifierV2 } from "../../support/types/project.types";
import { User } from "../../support/types/user.types";
import {
  createProjectIfMissingAPIV2,
  deleteProjectFromAPIV2,
  getProjectByNamespaceAPIV2,
} from "../../support/utils/projectsV2.utils";

const sessionId = ["codeRepositories", getRandomString()];

describe("Code repositories - work with code repositories", () => {
  // Define some project details
  const projectNameRandomPart = getRandomString();
  const projectName = `project-${projectNameRandomPart}`;
  const projectDescription =
    "This is a test project from Cypress to test working with code, data, environments";
  const projectIdentifier: ProjectIdentifierV2 = {
    slug: projectName,
    id: null,
    namespace: null,
  };

  // Create a project and keep that around for the rest of the tests
  before(() => {
    // Login
    cy.session(
      sessionId,
      () => {
        cy.robustLogin("v2");
      },
      validateLoginV2,
    );

    // Create the project and save its details
    getUserData().then((user: User) => {
      projectIdentifier.namespace = user.username;
      createProjectIfMissingAPIV2({
        description: projectDescription,
        name: projectName,
        namespace: user.username,
        slug: projectName,
        visibility: "private",
      }).then((project) => (projectIdentifier.id = project.id));
    });
  });

  // Restore the session (login)
  beforeEach(() => {
    cy.session(
      sessionId,
      () => {
        cy.robustLogin("v2");
      },
      validateLoginV2,
    );
  });

  // Cleanup the project after the test -- useful on failure
  after(() => {
    getProjectByNamespaceAPIV2(projectIdentifier).then((response) => {
      if (response.status === 200) {
        projectIdentifier.id = response.body.id;
        projectIdentifier.namespace = response.body.namespace;
        deleteProjectFromAPIV2(projectIdentifier);
      }
    });
  });

  const visitCurrentProject = () => {
    // There should a link on the dashboard for a newly created project
    cy.visit("/v2");
    cy.getDataCy("dashboard-project-list")
      .get(
        `a[href*="/${projectIdentifier.namespace}/${projectIdentifier.slug}"]`,
      )
      .click();
    cy.getDataCy("project-name").should("contain", projectName);
  };

  it("Add and modify code repositories", () => {
    const repoUrl = "https://github.com/SwissDataScienceCenter/renku-ui.git";
    const repoName = "renku-ui";
    const repoUrlEdited =
      "https://github.com/SwissDataScienceCenter/renku-data-services.git";
    const repoNameEdited = "renku-data-services";

    // Add code repository
    visitCurrentProject();
    cy.getDataCy("add-code-repository").click();
    cy.getDataCy("project-add-repository-url").should("be.empty").type(repoUrl);
    cy.getDataCy("add-code-repository-modal-button").click();
    cy.getDataCy("code-repositories-box")
      .find("[data-cy=code-repository-item]")
      .contains(repoName);
    cy.contains("[data-cy=code-repository-item]", repoName).contains(
      "Pull only",
    );

    // Edit code repository
    cy.contains("[data-cy=code-repository-item]", repoName)
      .find("[data-cy=code-repository-edit]")
      .click();
    cy.getDataCy("project-edit-repository-url")
      .should("have.value", repoUrl)
      .clear()
      .type(repoUrlEdited);
    cy.getDataCy("edit-code-repository-modal-button").click();
    cy.getDataCy("code-repositories-box")
      .find("[data-cy=code-repository-item]")
      .contains(repoNameEdited);
    cy.contains("[data-cy=code-repository-item]", repoNameEdited).contains(
      "Pull only",
    );
  });
});
