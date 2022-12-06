import { rstudioTestFuncs } from "@renku/notebooks-cypress-tests";
import { v4 as uuidv4 } from "uuid";
import { TIMEOUTS } from "../../config";

const username = Cypress.env("TEST_USERNAME");
const projectName = `test-project-${uuidv4()}`;

describe("Basic rstudio functionality", () => {
  before(() => {
    // Save all cookies across tests
    Cypress.Cookies.defaults({
      preserve: (_) => true
    });
  });

  afterEach(() => {
    cy.logout();
  });
  beforeEach(() => {
    cy.robustLogin();
  });

  it("Creates a project and launches an RStudio session", { defaultCommandTimeout: TIMEOUTS.standard }, () => {
    // Creates the project
    const templateName = "Basic R";
    cy.visit("/");
    cy.get(`[data-cy=username-home]`).should("include.text", username);
    const projectInfo = { name: projectName, namespace: username, templateName };
    cy.createProject(projectInfo);

    // Waits for the image to build
    cy.waitForImageToBuild(projectInfo);

    // Launches a session
    cy.startSession(projectInfo);

    // Opens the session in an iframe
    cy.contains("Open").click();
    cy.get("div.details-progress-box", { timeout: TIMEOUTS.vlong }).should("not.exist");
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
    // Deletes the project
    cy.deleteProject(projectInfo);
    // Stops the session
    cy.stopSessionFromIframe();
  });
});
