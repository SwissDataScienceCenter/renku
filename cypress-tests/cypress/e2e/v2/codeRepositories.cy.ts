import { getRandomString, getUserData } from "../../support/commands/general";
import { User } from "../../support/types/user";
import {
  createProjectIfMissingV2,
  deleteProject,
} from "../../support/utils/projects";
import { login } from "../../support/utils/general";

const sessionId = ["codeRepositories", getRandomString()];

describe("Code repositories", () => {
  // Define some project details
  const projectName = `project-${getRandomString()}`;
  let projectId: string;

  // Create a project and keep that around for the rest of the tests
  before(() => {
    login(sessionId);

    // Create the project and save its details
    getUserData().then((user: User) => {
      createProjectIfMissingV2({
        description: "Test project from Cypress to test code repositories",
        name: projectName,
        namespace: user.username,
        slug: projectName,
      }).then((response) => {
        projectId = response.body.id;
      });
    });
  });

  beforeEach(() => {
    login(sessionId);
  });

  after(() => {
    deleteProject(projectId);
  });

  it("Add and modify code repositories", () => {
    const repoUrl = "https://github.com/SwissDataScienceCenter/renku-ui.git";
    const repoName = "renku-ui";
    const repoUrlEdited =
      "https://github.com/SwissDataScienceCenter/renku-data-services.git";
    const repoNameEdited = "renku-data-services";

    // Add code repository
    cy.visitProjectByName(projectName);
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
