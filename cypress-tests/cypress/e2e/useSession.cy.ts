import { TIMEOUTS } from "../../config";
import { generatorProjectName } from "../support/commands/projects";
import { validateLogin } from "../support/commands/general";

const username = Cypress.env("TEST_USERNAME");

const projectTestConfig = {
  shouldCreateProject: true,
  projectName: generatorProjectName("useSession"),
};
const workflow = {
  name: "dummyworkflow8",
  output: "o8.txt"
};

// ? Modify the config -- useful for debugging
// projectTestConfig.shouldCreateProject = false;
// projectTestConfig.projectName = "cypress-publicproject-eecb9dd1c738";

const projectIdentifier = {
  name: projectTestConfig.projectName,
  namespace: username,
};

describe("Basic public project functionality", () => {
  before(() => {
    // Use a session to preserve login data
    cy.session(
      "login-useSession",
      () => {
        cy.robustLogin();
      },
      validateLogin
    );

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
    // Restore the session
    cy.session(
      "login-useSession",
      () => {
        cy.robustLogin();
      },
      validateLogin
    );
    cy.visitAndLoadProject(projectIdentifier);
    cy.stopAllSessionsForProject(projectIdentifier);
  });

  it("Start a new session on the project and interact with the terminal.", () => {
    // Start a session with options
    let serversInvoked = false;
    cy.intercept("/ui-server/api/notebooks/servers*", (req) => {
      serversInvoked = true;
    }).as("getServers");
    if (projectTestConfig.shouldCreateProject) {
      cy.dataCy("project-overview-content")
        .contains("your new Renku project", { timeout: TIMEOUTS.long })
        .should("exist");
    }
    cy.getProjectPageLink(projectIdentifier, "sessions")
      .should("exist")
      .click();
    if (serversInvoked) cy.wait("@getServers");
    cy.getProjectPageLink(projectIdentifier, "sessions/new")
      .should("exist")
      .first()
      .click();

    // Wait for the image to be ready and start a session
    cy.get(".renku-container")
      .contains("A session gives you an environment")
      .should("exist");
    cy.get(".renku-container .badge.bg-success", { timeout: TIMEOUTS.vlong })
      .contains("available")
      .should("exist");
    cy.get(".renku-container button.btn-rk-green", { timeout: TIMEOUTS.long })
      .contains("Start session")
      .should("exist")
      .click();
    cy.get(".progress-box .progress-title").should("exist"); //.contains("Step 2 of 2");
    cy.get(".fullscreen-header")
      .should("exist")
      .contains(projectTestConfig.projectName)
      .should("exist");
    cy.get(".progress-box .progress-title")
      .contains("Starting Session")
      .should("exist");
    cy.get(".progress-box .progress-title", { timeout: TIMEOUTS.vlong }).should(
      "not.exist"
    );

    // Verify the "Connect" button works as well
    cy.get(".fullscreen-header")
      .should("exist")
      .get(".fullscreen-back-button")
      .contains("Back")
      .should("exist")
      .click();
    cy.dataCy("open-session").should("exist").click();
    cy.get(".progress-box .progress-title")
      .contains("Starting Session")
      .should("exist");

    // run a simple workflow in the iframe
    cy.getIframe("iframe#session-iframe").within(() => {
      // open the terminal and check the repo is not ahead
      cy.get(".jp-Launcher-content", { timeout: TIMEOUTS.long }).should(
        "be.visible"
      );
      cy.get(".jp-Launcher-section").should("be.visible");
      cy.get('.jp-LauncherCard[title="Start a new terminal session"]')
        .should("be.visible")
        .click();

      // run a dummy workflow
      cy.get(".xterm-helper-textarea")
        .click()
        .type(`renku run --name ${workflow.name} echo "123" > ${workflow.output}{enter}`);
      cy.get("#filebrowser").should("be.visible").contains(workflow.output).should("be.visible");
      cy.get("#jp-git-sessions")
        .get(`button[title="Push committed changes (ahead by 1 commits)"]`)
        .should("not.exist");

      // push the changes
      cy.get(`[data-id="jp-git-sessions"]`).should("be.visible").click();
      cy.get("#jp-git-sessions").contains(projectTestConfig.projectName).should("be.visible");
      cy.get("#jp-git-sessions")
        .get(`button[title="Push committed changes (ahead by 1 commits)"]`)
        .should("exist")
        .click();
      cy.get("#jp-git-sessions")
        .get(`button[title="Push committed changes"]`, { timeout: TIMEOUTS.long })
        .should("exist");
      cy.get("#jp-git-sessions")
        .get(`button[title="Push committed changes (ahead by 1 commits)"]`)
        .should("not.exist");
    });

    // stop the session
    cy.dataCy("stop-session-button").should("exist").click();
    cy.dataCy("stop-session-modal-button").should("exist").click();
    cy.dataCy("stopping-btn").should("exist");
    cy.get(".renku-container", { timeout: TIMEOUTS.long })
      .should("exist")
      .contains("No currently running sessions")
      .should("exist");

    // check the workflows tab
    cy.dataCy("go-back-button").click();
    cy.dataCy("project-navbar", true)
      .contains("a.nav-link", "Workflows")
      .should("exist")
      .click();
    // TODO
  });
});
