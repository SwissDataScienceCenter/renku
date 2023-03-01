import { rstudioTestFuncs } from "@renku/notebooks-cypress-tests";
import { v4 as uuidv4 } from "uuid";
import { TIMEOUTS } from "../../config";

const username = Cypress.env("TEST_USERNAME");
const firstname = Cypress.env("TEST_FIRST_NAME");
const lastname = Cypress.env("TEST_LAST_NAME");
const projectName = `test-project-${uuidv4()}`;

describe("Basic rstudio functionality", () => {
  before(() => {
    // Save all cookies across tests
    Cypress.Cookies.defaults({
      preserve: (_) => true
    });
  });

  after(() => {
    cy.logout();
  });
  before(() => {
    cy.robustLogin();
  });

  it("Creates a project and launches an RStudio session", { defaultCommandTimeout: TIMEOUTS.long }, () => {
    // Creates the project
    const templateName = "Basic R";
    cy.visit("/");
    cy.get(`[data-cy=dashboard-title]`).should("include.text", `Renku Dashboard - ${firstname} ${lastname}`);
    const projectInfo = { name: projectName, namespace: username, templateName };
    cy.createProject(projectInfo);

    // Waits for the image to build
    cy.waitForImageToBuild(projectInfo);

    // Launches a session
    cy.startSession(projectInfo);

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
    // Deletes the project
    cy.deleteProject(projectInfo);
  });
});
