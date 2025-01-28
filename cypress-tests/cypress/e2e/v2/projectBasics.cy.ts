import { getRandomString, validateLoginV2 } from "../../support/commands/general";
import { deleteProjectFromAPIV2, getProjectByNamespaceAPIV2, ProjectIdentifierV2 } from "../../support/utils/projectsV2.utils";

// ! const sessionId = ["dashboardV2", getRandomString()];
const sessionId = ["dashboardV2", "123"];
const username = Cypress.env("TEST_USERNAME");

beforeEach(() => {
  // Restore the session
  cy.session(
    sessionId,
    () => {
      cy.robustLogin();
    },
    validateLoginV2
  );
});


describe("Project - create, edit and delete", () => {
  const projectNameRandomBits = getRandomString();
  const projectName = `project/$test-${projectNameRandomBits}`;
  const projectPath = `project-test-${projectNameRandomBits}`;
  const projectDescription = "This is a test project from Cypress";

  const projectIdentifier: ProjectIdentifierV2 = {
    slug: projectPath,
    id: null,
    namespace: username,
  };

  after(() => {
    getProjectByNamespaceAPIV2(projectIdentifier).then((response) => {
      if (response.status === 200) {
        projectIdentifier.id = response.body.id;
        deleteProjectFromAPIV2(projectIdentifier);
      }
    });
  });

  it("Create a project", () => {
    // Create a new project
    cy.visit("/v2");
    cy.get("#plus-dropdown").should("exist").click();
    cy.getDataCy("navbar-project-new").click();
    cy.location("hash").should("eq", "#create-project");
    cy.getDataCy("new-project-modal")
      .getDataCy("new-project-modal-header")
      .should("exist");
    cy.getDataCy("project-name-input").type(projectName);
    cy.getDataCy("project-slug-toggle").click();
    cy.getDataCy("project-slug-input").should("have.value", projectPath);
    cy.getDataCy("project-visibility-public").click();
    cy.getDataCy("project-description-input").type(projectDescription);
    cy.getDataCy("new-project-modal-body").contains(
      `The URL for this project will be renkulab.io/v2/projects/${username}/${projectPath}`
    );
    cy.getDataCy("project-create-button").click();
    cy.location("pathname").should(
      "eq",
      `/v2/projects/${username}/${projectPath}`
    );

    // Change settings

    // Delete project
    cy.getDataCy("project-settings-link").click();
    cy.getDataCy("project-delete")
      .should("exist")
      .contains("Are you sure you want to delete this project?");
    cy.getDataCy("project-delete-button").should("not.be.enabled");
    cy.getDataCy("delete-confirmation-input").type(projectPath);
    cy.intercept("DELETE", /(?:\/ui-server)?\/api\/data\/projects\/[^/]+/).as("deleteProject");
    cy.getDataCy("project-delete-button").should("be.enabled").click();
    cy.wait("@deleteProject");
    getProjectByNamespaceAPIV2(projectIdentifier).then((response) => {
      expect(response.status).to.equal(404);
    });
  });
});

