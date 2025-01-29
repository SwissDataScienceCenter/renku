import {
  getRandomString,
  validateLoginV2,
} from "../../support/commands/general";
import { ProjectIdentifierV2 } from "../../support/types/project.types";
import { User } from "../../support/types/user.types";
import {
  deleteProjectFromAPIV2,
  getProjectByNamespaceAPIV2,
} from "../../support/utils/projectsV2.utils";

const sessionId = ["projectBasics", getRandomString()];

beforeEach(() => {
  // Restore the session (login)
  cy.session(
    sessionId,
    () => {
      cy.robustLogin();
    },
    validateLoginV2
  );
});

describe("Project - create, edit and delete", () => {
  // Define some project details
  const projectNameRandomPart = getRandomString();
  const projectName = `project/$test-${projectNameRandomPart}`;
  const projectPath = `project-test-${projectNameRandomPart}`;
  const projectDescription = "This is a test project from Cypress";
  const projectIdentifier: ProjectIdentifierV2 = {
    slug: projectPath,
    id: null,
    namespace: null,
  };

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

  it("Project - create, edit and delete", () => {
    // Create a new project
    cy.visit("/v2");
    cy.getUserData().then((user: User) => {
      const username = user.username;
      cy.get("#plus-dropdown").should("exist").click();
      cy.getDataCy("navbar-project-new").click();
      cy.location("hash").should("eq", "#create-project");
      cy.getDataCy("new-project-modal").should("exist");
      cy.getDataCy("project-name-input").type(projectName);
      cy.getDataCy("project-slug-toggle").click();
      cy.getDataCy("project-slug-input").should("have.value", projectPath);
      cy.getDataCy("project-visibility-public").click();
      cy.getDataCy("project-description-input").type(projectDescription);
      cy.getDataCy("new-project-modal").contains(
        `The URL for this project will be renkulab.io/v2/projects/${username}/${projectPath}`
      );
      cy.getDataCy("project-create-button").click();
      cy.location("pathname").should(
        "eq",
        `/v2/projects/${username}/${projectPath}`
      );

      // Change settings
      const modifiedProjectName = `${projectName} - modified`;
      const modifiedProjectDescription = `${projectDescription} - modified`;
      cy.getDataCy("project-settings-link").click();
      cy.getDataCy("project-name-input").should("have.value", projectName);
      cy.getDataCy("project-name-input").clear().type(modifiedProjectName);

      cy.getDataCy("project-description-input").should(
        "have.value",
        projectDescription
      );
      cy.getDataCy("project-description-input")
        .clear()
        .type(modifiedProjectDescription);

      cy.get("#project-visibility-public").should("be.checked");
      cy.getDataCy("project-visibility-private").click();

      cy.intercept("PATCH", /(?:\/ui-server)?\/api\/data\/projects\/[^/]+/).as(
        "updateProject"
      );
      cy.getDataCy("project-update-button").click();
      cy.wait("@updateProject");
      cy.getDataCy("project-settings-general")
        .get(".alert-success")
        .contains("The project has been successfully updated.");
      cy.getDataCy("project-name").should("contain", modifiedProjectName);
      cy.getDataCy("project-description").should(
        "contain",
        modifiedProjectDescription
      );

      // Revert settings
      cy.getDataCy("project-overview-link").click();
      cy.getDataCy("project-settings-link").click();
      cy.getDataCy("project-name-input").clear().type(projectName);
      cy.getDataCy("project-description-input")
        .clear()
        .type(projectDescription);
      cy.getDataCy("project-visibility-public").click();
      cy.getDataCy("project-update-button").click();
      cy.wait("@updateProject");

      cy.getDataCy("project-name").should("contain", projectName);
      cy.getDataCy("project-description").should("contain", projectDescription);

      // Delete project
      cy.getDataCy("project-settings-link").click();
      cy.getDataCy("project-delete")
        .should("exist")
        .contains("Are you sure you want to delete this project?");
      cy.getDataCy("project-delete-button").should("not.be.enabled");
      cy.getDataCy("delete-confirmation-input").type(projectPath);
      cy.intercept("DELETE", /(?:\/ui-server)?\/api\/data\/projects\/[^/]+/).as(
        "deleteProject"
      );
      cy.getDataCy("project-delete-button").should("be.enabled").click();
      cy.wait("@deleteProject");
      getProjectByNamespaceAPIV2(projectIdentifier).then((response) => {
        expect(response.status).to.equal(404);
      });
    });
  });
});
