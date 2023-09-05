import { TIMEOUTS } from "../../config";
import {
  ProjectIdentifier,
  generatorProjectName
} from "../support/commands/projects";
import { validateLogin } from "../support/commands/general";

const username = Cypress.env("TEST_USERNAME");

const projectTestConfig = {
  shouldCreateProject: true,
  projectName: generatorProjectName("privateProject"),
};

// ? Uncomment to debug using an existing project
// projectTestConfig.shouldCreateProject = false;
// projectTestConfig.projectName = "cypress-privateproject-ad99f11c1482";

const projectIdentifier: ProjectIdentifier = {
  name: projectTestConfig.projectName,
  namespace: username,
};

describe("Basic public project functionality", () => {
  before(() => {
    // Use a session to preserve login data
    cy.session(
      "login-publicProject",
      () => {
        cy.robustLogin();
      },
      validateLogin
    );

    // Create a project for the local spec
    if (projectTestConfig.shouldCreateProject) {
      cy.visit("/");
      cy.createProject({ templateName: "Python", ...projectIdentifier, visibility: "private" });
    }
  });

  beforeEach(() => {
    // Restore the session
    cy.session(
      "login-publicProject",
      () => {
        cy.robustLogin();
      },
      validateLogin
    );
    cy.visitAndLoadProject(projectIdentifier);
  });

  it("Can search for project only when logged in", () => {
    // Assess the project has been indexed properly
    cy.dataCy("project-navbar", true)
      .contains("a.nav-link", "Settings")
      .should("be.visible")
      .click();
    cy.dataCy("project-settings-knowledge-graph")
      .contains("Project indexing", { timeout: TIMEOUTS.vlong })
      .should("exist");
    cy.dataCy("kg-status-section-open").should("exist").click();
    cy.dataCy("project-settings-knowledge-graph")
      .contains("Everything indexed", { timeout: TIMEOUTS.vlong })
      .should("exist");
    cy.dataCy("visibility-private").should("be.visible").should("be.checked");
    cy.searchForProject(projectIdentifier, true);

    // logout and search for the project and log back in
    cy.logout();
    cy.get("#nav-hamburger").should("be.visible").click();
    cy.searchForProject(projectIdentifier, false);
    cy.robustLogin();
  });

  it("Can always search for project after changing the visibility", () => {
    // Change visibility to public
    cy.dataCy("project-navbar", true)
      .contains("a.nav-link", "Settings")
      .should("be.visible")
      .click();
    cy.dataCy("project-settings-knowledge-graph")
      .contains("Project indexing", { timeout: TIMEOUTS.vlong })
      .should("exist");
    cy.dataCy("visibility-private").should("be.visible").should("be.checked");
    cy.dataCy("visibility-public").should("be.visible").check();
    cy.get(".modal")
      .contains("Change visibility to Public")
      .should("be.visible");
    cy.dataCy("update-visibility-btn").should("be.visible").click();
    cy.get(".modal .alert-success", { timeout: TIMEOUTS.long })
      .contains("The visibility of the project has been modified")
      .should("be.visible");
    // ? We need to wait before other checks take place.
    // ? This is a workaround until we use the new Project update endpoint.
    // ? Reference: https://github.com/SwissDataScienceCenter/renku-ui/issues/2778
    cy.wait(TIMEOUTS.standard * 0.5); // eslint-disable-line cypress/no-unnecessary-waiting

    // Check all is up-to-date and ready.
    cy.get(".modal button.btn-close").should("be.visible").click();
    cy.dataCy("kg-status-section-open").should("exist").click();
    cy.dataCy("project-settings-knowledge-graph")
      .contains("Everything indexed", { timeout: TIMEOUTS.vlong })
      .should("exist");
    cy.dataCy("visibility-private")
      .should("be.visible")
      .should("not.be.checked");
    cy.dataCy("visibility-public").should("be.visible").should("be.checked");

    // Search the project as both logged in and logged out.
    cy.searchForProject(projectIdentifier, true);
    cy.logout();
    cy.get("#nav-hamburger").should("be.visible").click();
    cy.searchForProject(projectIdentifier, false);
    cy.robustLogin();

  });

  it("Deleting the project removes it from the search page", () => {
    // Delete the project
    cy.visitAndLoadProject(projectIdentifier);

    const slug = projectIdentifier.namespace + "/" + projectIdentifier.name;
    cy.intercept("DELETE", `/ui-server/api/kg/projects/${slug}`).as(
      "deleteProject"
    );
    cy.dataCy("project-navbar", true)
      .contains("a.nav-link", "Settings")
      .should("exist")
      .click();
    cy.dataCy("project-settings-general-delete-project")
      .should("be.visible")
      .find("button")
      .contains("Delete project")
      .should("be.visible")
      .click();
    cy.contains("Are you absolutely sure?");
    cy.get("input[name=project-settings-general-delete-confirm-box]").type(
      slug
    );
    cy.get("button")
      .contains("Yes, delete this project")
      .should("be.visible")
      .should("be.enabled")
      .click();
    cy.wait("@deleteProject");

    cy.url().should("not.contain", `/projects/${slug}`);
    cy.get(".Toastify")
      .contains(`Project ${slug} deleted`)
      .should("be.visible");

    // Check that the project is not listed anymore on the search page
    cy.searchForProject(projectIdentifier, false);
  });
});
