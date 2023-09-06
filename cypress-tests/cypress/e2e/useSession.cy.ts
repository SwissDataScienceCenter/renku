import { TIMEOUTS } from "../../config";
import { generatorProjectName } from "../support/commands/projects";
import { validateLogin } from "../support/commands/general";

const username = Cypress.env("TEST_USERNAME");

const projectTestConfig = {
  shouldCreateProject: true,
  projectName: generatorProjectName("useSession"),
};
const workflow = {
  name: "dummyworkflow",
  output: "o.txt", // ? Keep the name short or it won't show up entirely in the file browser
};

// ? Modify the config -- useful for debugging
projectTestConfig.shouldCreateProject = false;
projectTestConfig.projectName = "test-session-00001";

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
    cy.wait("@getDatasets");
    cy.wait(5_000, { log: false }); // eslint-disable-line cypress/no-unnecessary-waiting
    cy.get("button.startButton")
      .dataCy("more-menu")
      .should("be.visible")
      .click();
    cy.getProjectPageLink(projectIdentifier, "sessions/new")
      .should("be.visible")
      .click();

    // Wait for the image to be ready and start a session
    cy.get(".renku-container")
      .contains("A session gives you an environment")
      .should("exist");
    cy.get(".renku-container .badge.bg-success", { timeout: TIMEOUTS.vlong })
      .contains("available")
      .should("exist");
    cy.get(".renku-container button.btn-secondary", { timeout: TIMEOUTS.long })
      .contains("Start Session")
      .should("exist")
      .click();
    cy.get(".progress-box .progress-title").should("exist"); //.contains("Step 2 of 2");
    cy.get("button")
      .contains(projectTestConfig.projectName)
      .should("be.visible");
    cy.get(".progress-box .progress-title")
      .contains("Starting Session")
      .should("exist");
    cy.get(".progress-box .progress-title", { timeout: TIMEOUTS.vlong }).should(
      "not.exist"
    );

    // Verify the "Connect" button works as well
    cy.get(".fullscreen-back-button").contains("Back").should("exist").click();
    cy.dataCy("open-session").first().should("be.visible").click();
    cy.get(".progress-box .progress-title")
      .contains("Starting Session")
      .should("exist");

    // Run a simple workflow in the iframe
    cy.getIframe("iframe#session-iframe").within(() => {
      // Open the terminal and check the repo is not ahead
      cy.get(".jp-Launcher-content", { timeout: TIMEOUTS.long }).should(
        "be.visible"
      );
      cy.get(".jp-Launcher-section").should("be.visible");
      cy.get('.jp-LauncherCard[title="Start a new terminal session"]')
        .should("be.visible")
        .click();

      // Run a dummy workflow
      cy.get(".xterm-helper-textarea")
        .click()
        .type(
          `renku run --name ${workflow.name} echo "123" > ${workflow.output}{enter}`
        );
      cy.get("#filebrowser")
        .should("be.visible")
        .contains(workflow.output)
        .should("be.visible");
      cy.get("#jp-git-sessions")
        .get(`button[title="Push committed changes (ahead by 1 commits)"]`)
        .should("not.exist");

      // Push the changes
      // ? Switch to using the Save session button as soon as it works again.
      // ? Reference: https://github.com/SwissDataScienceCenter/renku-notebooks/issues/1575
      // // cy.dataCy("save-session-button").should("be.visible").click();
      // // cy.get(".modal-session").contains("1 commit will be pushed").should("be.visible");
      // // cy.dataCy("save-session-modal-button").should("be.visible").click();
      cy.get(`[data-id="jp-git-sessions"]`).should("be.visible").click();
      cy.get("#jp-git-sessions")
        .contains(projectTestConfig.projectName)
        .should("be.visible");
      cy.get("#jp-git-sessions")
        .get(`button[title="Push committed changes (ahead by 1 commits)"]`)
        .should("exist")
        .click();
      cy.get("#jp-git-sessions")
        .get(`button[title="Push committed changes"]`, {
          timeout: TIMEOUTS.long,
        })
        .should("exist");
      cy.get("#jp-git-sessions")
        .get(`button[title="Push committed changes (ahead by 1 commits)"]`)
        .should("not.exist");
    });

    // Pause the session
    cy.dataCy("pause-session-button").should("be.visible").click();
    cy.dataCy("pause-session-modal-button").should("be.visible").click();

    cy.get('[data-cy="session-container"]', { timeout: TIMEOUTS.long })
      .should("be.visible")
      .contains("Paused");

    // Stop the session
    cy.dataCy("more-menu").first().should("be.visible").click();
    cy.dataCy("delete-session-button").first().should("be.visible").click();
    cy.dataCy("delete-session-modal-button").should("exist").click();
    cy.dataCy("stopping-btn").should("exist");
    cy.get(".renku-container", { timeout: TIMEOUTS.long })
      .should("exist")
      .contains("No currently running sessions")
      .should("exist");

    // Go the the workflows page and check the new workflow appears
    cy.dataCy("project-navbar")
      .contains("a.nav-link", "Workflows")
      .should("be.visible")
      .click();

    cy.dataCy("workflows-browser")
      .should("be.visible")
      .children()
      .should("have.length", 1)
      .contains(workflow.name)
      .should("be.visible")
      .click();

    cy.dataCy("workflow-details")
      .should("be.visible")
      .contains(`echo 123 > ${workflow.output}`)
      .should("be.visible");
  });
});
