import { v4 as uuidv4 } from "uuid";
import { TIMEOUTS } from "../../config";

const username = Cypress.env("TEST_USERNAME");

const projectTestConfig = {
  shouldCreateProject: true,
  projectName: `test-session-${uuidv4().substring(24)}`
};

// ? Modify the config -- useful for debugging
// projectTestConfig.shouldCreateProject = false;
// projectTestConfig.projectName = "test-session-4f79daad6d4e";

const projectIdentifier = { name: projectTestConfig.projectName, namespace: username };

describe("Basic public project functionality", () => {
  before(() => {
    Cypress.Cookies.debug(true);
    // Save all cookies across tests
    Cypress.Cookies.defaults({
      preserve: (_) => true,
    });
    // Register with the CI deployment
    cy.robustLogin();

    // Create a project
    if (projectTestConfig.shouldCreateProject) {
      cy.visit("/");
      cy.createProject({ templateName: "Python", ...projectIdentifier });
    }
  });

  after(() => {
    if (projectTestConfig.shouldCreateProject)
      cy.deleteProject(projectIdentifier);
  });

  beforeEach(() => {
    cy.visitAndLoadProject(projectIdentifier);
    cy.stopAllSessionsForProject(projectIdentifier);
  });

  it("Start a new session on the project and interact with the terminal.", () => {
    // Start a session with options
    let serversInvoked = false;
    cy.intercept("/ui-server/api/notebooks/servers*", req => { serversInvoked = true; }).as("getServers");
    cy.dataCy("project-overview-content")
      .contains("your new Renku project", { timeout: TIMEOUTS.long }).should("exist");
    cy.getProjectPageLink(projectIdentifier, "sessions").should("exist").click();
    if (serversInvoked)
      cy.wait("@getServers");
    cy.getProjectPageLink(projectIdentifier, "sessions/new").should("exist").first().click();

    // Wait for the image to be ready and start a session
    cy.get(".renku-container").contains("A session gives you an environment").should("exist");
    cy.get(".renku-container .badge.bg-success", { timeout: TIMEOUTS.vlong })
      .contains("available").should("exist");
    cy.get(".renku-container button.btn-rk-green", { timeout: TIMEOUTS.long })
      .contains("Start session").should("exist").click();
    cy.get(".progress-box .progress-title").should("exist"); //.contains("Step 2 of 2");
    cy.get(".fullscreen-header").should("exist").contains(projectTestConfig.projectName).should("exist");
    cy.get(".progress-box .progress-title").contains("Starting Session").should("exist");
    cy.get(".progress-box .progress-title", { timeout: TIMEOUTS.vlong }).should("not.exist");

    // Verify the "Connect" button works as well
    cy.get(".fullscreen-header").should("exist").get(".fullscreen-back-button").contains("Back")
      .should("exist").click();
    cy.dataCy("open-session").should("exist").click();
    cy.get(".progress-box .progress-title").contains("Starting Session").should("exist");

    // run a simple workflow in the iframe
    cy.getIframe("iframe#session-iframe").within(() => {
      cy.get(".jp-Launcher-content", { timeout: TIMEOUTS.long }).should("exist");
      cy.get(".jp-Launcher-section").should("exist");
      cy.get('.jp-LauncherCard[title="Start a new terminal session"]').should("exist").click();
      // TODO: use the terminal to execute a simple workflow
      // ? /SwissDataScienceCenter/notebooks-cypress-tests/blob/main/cypress/support/commands/jupyterlab.ts
    });

    cy.dataCy("stop-session-button").should("exist").click();
    cy.dataCy("stop-session-modal-button").should("exist").click();
    cy.dataCy("stopping-btn").should("exist");
    cy.get(".renku-container", { timeout: TIMEOUTS.long }).should("exist")
      .contains("No currently running sessions").should("exist");
  });
});
