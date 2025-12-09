import { getRandomString, getUserData } from "../../support/commands/general";
import { User } from "../../support/types/user";
import { deleteProject } from "../../support/utils/projects";
import { login } from "../../support/utils/general";

const sessionId = ["projects", getRandomString()];

describe("Project - create, edit and delete", () => {
  // Define some project details
  const projectDescription = "This is a test project from Cypress";
  let projectNameRandomPart: string;
  let projectName: string;
  let projectPath: string;
  let projectId: string | null = null;

  function resetRequiredResources() {
    projectNameRandomPart = getRandomString();
    projectName = `project/$test-${projectNameRandomPart}`;
    projectPath = `project-test-${projectNameRandomPart}`;
    projectId = null;
  }

  beforeEach(() => {
    login(sessionId);

    // Define new project details to avoid conflicts on retries
    resetRequiredResources();
  });

  // Cleanup the project after the test -- useful on failure
  afterEach(() => {
    deleteProject(projectId);
  });

  it("Project - create, edit and delete", () => {
    // Create a new project
    cy.visit("/v2");
    getUserData().then((user: User) => {
      const username = user.username;
      cy.getDataCy("navbar-new-entity").click();
      cy.getDataCy("navbar-project-new").click();
      cy.getDataCy("project-creation-form").should("exist");
      cy.getDataCy("project-creation-form-project-name-input").type(
        projectName,
      );
      cy.getDataCy("project-slug-toggle").click();
      cy.getDataCy("project-slug-input").should("have.value", projectPath);
      cy.getDataCy("project-visibility-public").click();
      cy.getDataCy("project-creation-form-project-description-input").type(
        projectDescription,
      );
      cy.getDataCy("project-url-preview").contains(
        `/${username}/${projectPath}`,
      );
      cy.intercept("POST", /(?:\/ui-server)?\/api\/data\/projects/).as(
        "createProject",
      );
      cy.getDataCy("project-create-button").click();
      cy.wait("@createProject");
      cy.getDataCy("project-name").should("contain", projectName);

      // Change settings
      const modifiedProjectName = `${projectName} - modified`;
      const modifiedProjectDescription = `${projectDescription} - modified`;
      cy.getDataCy("project-settings-link").click();
      cy.getDataCy("project-settings-form-project-name-input").should(
        "have.value",
        projectName,
      );
      cy.getDataCy("project-settings-form-project-name-input")
        .clear()
        .type(modifiedProjectName);

      cy.getDataCy("project-settings-form-project-description-input").should(
        "have.value",
        projectDescription,
      );
      cy.getDataCy("project-settings-form-project-description-input")
        .clear()
        .type(modifiedProjectDescription);

      cy.get("#project-settings-form-project-visibility-public").should(
        "be.checked",
      );
      cy.getDataCy("project-visibility-private").click();

      cy.intercept("PATCH", /(?:\/ui-server)?\/api\/data\/projects\/[^/]+/).as(
        "updateProject",
      );
      cy.getDataCy("project-update-button").click();
      cy.wait("@updateProject");
      cy.getDataCy("project-settings-general")
        .get(".alert-success")
        .contains("The project has been successfully updated.");
      cy.getDataCy("project-name").should("contain", modifiedProjectName);
      cy.getDataCy("project-description").should(
        "contain",
        modifiedProjectDescription,
      );

      // Delete project
      cy.getDataCy("project-settings-link").click();
      cy.getDataCy("project-delete");
      cy.getDataCy("project-delete-button").should("not.be.enabled");
      cy.getDataCy("delete-confirmation-input").type(projectPath);
      cy.intercept("DELETE", /(?:\/ui-server)?\/api\/data\/projects\/[^/]+/).as(
        "deleteProject",
      );
      cy.getDataCy("project-delete-button").should("be.enabled").click();
      cy.wait("@deleteProject");
      projectId = null; // Mark as deleted so afterEach doesn't try to delete again
    });
  });
});
