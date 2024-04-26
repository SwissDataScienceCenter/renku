import { rstudioTestFuncs } from "@renku/notebooks-cypress-tests";

import { TIMEOUTS } from "../../config";
import {
  ProjectIdentifier,
  generatorProjectName,
} from "../support/commands/projects";
import { validateLogin, getRandomString } from "../support/commands/general";

const username = Cypress.env("TEST_USERNAME");

const projectTestConfig = {
  projectAlreadyExists: false,
  projectName: generatorProjectName("rstudioSession"),
};

// ? Uncomment to debug using an existing project
// projectTestConfig.projectAlreadyExists = true;
// projectTestConfig.projectName = "cypress-publicproject-4ed4fb12c5e6";

const projectIdentifier: ProjectIdentifier = {
  name: projectTestConfig.projectName,
  namespace: username,
};

const sessionId = ["rstudioSession", getRandomString()];

describe("Basic rstudio functionality", () => {
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
    cy.createProject({ templateName: "R (", ...projectIdentifier });
    cy.stopAllSessionsForProject(projectIdentifier);
  });

  it(
    "Creates a project and launches an RStudio session",
    { defaultCommandTimeout: TIMEOUTS.long },
    () => {
      // Waits for the image to build and launches a session
      // Note: rstudio image may take a while to build
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
      cy.pauseSession();
      cy.deleteSession();
    }
  );
});
