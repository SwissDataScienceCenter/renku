import { getRandomString, getUserData } from "../../support/commands/general";
import { User } from "../../support/types/user";
import {
  createProjectIfMissingV2,
  deleteProject,
} from "../../support/utils/projects";
import { createCodeRepository } from "../../support/utils/codeRepositories";
import { login } from "../../support/utils/general";

const sessionId = ["codeRepositories", getRandomString()];

describe("Code repositories", () => {
  const projectName = `project-code-repository-tests-${getRandomString()}`;
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

  it("Add and delete code repository", () => {
    const repoUrl = "https://github.com/SwissDataScienceCenter/renku-gateway";
    const repoName = "renku-gateway";

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

    // Delete code repository
    cy.contains("[data-cy=code-repository-item]", repoName)
      .find("[data-cy=button-with-menu-dropdown]")
      .click();
    cy.getDataCy("code-repository-delete").click();
    cy.getDataCy("delete-code-repository-modal-button").click();

    // Verify the repository is deleted
    cy.getDataCy("code-repositories-box")
      .contains("[data-cy=code-repository-item]", repoName)
      .should("not.exist");
  });

  it("Edit code repository", () => {
    const repoUrl = "https://github.com/SwissDataScienceCenter/renku-ui.git";
    const repoName = "renku-ui";

    const newName = "renku-data-services";
    const newUrl = repoUrl.replace("renku-ui", newName);

    // Add code repository
    createCodeRepository(repoUrl, projectId);

    // Navigate to project and verify repository was added
    cy.visitProjectByName(projectName);
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
      .type(newUrl);
    cy.getDataCy("edit-code-repository-modal-button").click();
    cy.getDataCy("code-repositories-box")
      .find("[data-cy=code-repository-item]")
      .contains(newName);
    cy.contains("[data-cy=code-repository-item]", newName).contains(
      "Pull only",
    );
  });
});
