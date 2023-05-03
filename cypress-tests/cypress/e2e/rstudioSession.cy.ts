import { rstudioTestFuncs } from "@renku/notebooks-cypress-tests";
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

describe("Basic rstudio functionality", () => {
  before(() => {
    // Save all cookies across tests
    Cypress.Cookies.defaults({
      preserve: (_) => true,
    });
    // Register with the CI deployment
    cy.robustLogin();

    // Create a project
    if (projectTestConfig.shouldCreateProject) {
      cy.visit("/");
      cy.createProject({ templateName: "Basic R", ...projectIdentifier });
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

  it("Creates a project and launches an RStudio session", { defaultCommandTimeout: TIMEOUTS.long }, () => {
    // Waits for the image to build
    cy.waitForImageToBuild(projectIdentifier);

    // Launches a session
    cy.startSession(projectIdentifier);

    // Opens the session in an iframe
    cy.getIframe("iframe#session-iframe").within(() => {
      rstudioTestFuncs.findExpectedElements();
    });

    // Launches a terminal in the session iframe
    cy.getIframe("iframe#session-iframe").within(() => {
      rstudioTestFuncs.launchTerminal();
    });
    // Runs terminal command to create a file
    cy.getIframe("iframe#session-iframe").within(() => {
      rstudioTestFuncs.makeFileWithTerminal("new-file.txt")();
    });
    // Removes the file
    cy.getIframe("iframe#session-iframe").within(() => {
      rstudioTestFuncs.removeFileWithTerminal("new-file.txt")();
    });

    // Closes the terminal
    cy.getIframe("iframe#session-iframe").within(() => {
      rstudioTestFuncs.closeTerminal();
    });
    // Finds expected start page elements again
    cy.getIframe("iframe#session-iframe").within(() => {
      rstudioTestFuncs.findExpectedElements();
    });

    // Stops the session
    cy.stopSessionFromIframe();
  });
});
