import { TIMEOUTS } from "../../config";
import {
  ProjectIdentifier,
  generatorProjectName,
} from "../support/commands/projects";
import { validateLogin, getRandomString } from "../support/commands/general";

const username = Cypress.env("TEST_USERNAME");

const projectTestConfig = {
  projectAlreadyExists: false,
  projectName: generatorProjectName("privateProject"),
};

// ? Uncomment to debug using an existing project
// projectTestConfig.projectAlreadyExists = true;
// projectTestConfig.projectName = "cypress-privateproject-ad99f11c1482";

const projectIdentifier: ProjectIdentifier = {
  name: projectTestConfig.projectName,
  namespace: username,
};

const sessionId = ["privateProject", getRandomString()];

describe("Basic public project functionality", () => {
  after(() => {
    if (!projectTestConfig.projectAlreadyExists)
      cy.deleteProjectFromAPI(projectIdentifier);
  });

  beforeEach(() => {
    // Restore the session
    cy.session(
      sessionId,
      () => {
        cy.robustLogin();
      },
      validateLogin
    );
    cy.createProjectIfMissing({templateName: "Python", ...projectIdentifier, visibility: "private"});
    cy.visitAndLoadProject(projectIdentifier);
  });

  it("Can search for project only when logged in", () => {
    // Assess the project has been indexed properly
    cy.waitMetadataIndexing();
    cy.getDataCy("visibility-private")
      .should("be.visible")
      .should("be.checked");
    cy.searchForProject(projectIdentifier, true);

    // logout and search for the project and log back in
    cy.logout();
    cy.get("#nav-hamburger").should("be.visible").click();
    cy.searchForProject(projectIdentifier, false);
    cy.robustLogin();
  });

  it("Can always search for project after changing the visibility", () => {
    // Change visibility to public
    cy.waitMetadataIndexing();
    cy.getDataCy("visibility-private")
      .should("be.visible")
      .should("be.checked");
    cy.getDataCy("visibility-public").should("be.visible").check();
    cy.get(".modal")
      .contains("Change visibility to Public")
      .should("be.visible");
    cy.getDataCy("update-visibility-btn").should("be.visible").click();
    cy.get(".modal .alert-success", { timeout: TIMEOUTS.long })
      .contains("The visibility of the project has been modified")
      .should("be.visible");
    // ? We need to wait before other checks take place.
    // ? This is a workaround until we use the new Project update endpoint.
    // ? Reference: https://github.com/SwissDataScienceCenter/renku-ui/issues/2778
    cy.wait(TIMEOUTS.short); // eslint-disable-line cypress/no-unnecessary-waiting

    // Check all is up-to-date and ready.
    cy.get(".modal button.btn-close").should("be.visible").click();
    cy.waitMetadataIndexing();
    cy.getDataCy("visibility-private")
      .should("be.visible")
      .should("not.be.checked");
    cy.getDataCy("visibility-public").should("be.visible").should("be.checked");

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
    cy.deleteProject(projectIdentifier);

    // Check that the project is not listed anymore on the search page
    cy.searchForProject(projectIdentifier, false);
  });
});
