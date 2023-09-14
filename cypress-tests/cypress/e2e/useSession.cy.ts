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
// projectTestConfig.shouldCreateProject = false;
// projectTestConfig.projectName = "cypress-usesession-a8c6823e40ff";

const projectIdentifier = {
  name: projectTestConfig.projectName,
  namespace: username,
};

const projectWithoutPermissions = {
  namespace: "renku-ui-tests",
  name: "stable-project",
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
      cy.deleteProjectFromAPI(projectIdentifier);
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
  });

  it("Start a new session on the project and interact with the terminal.", () => {
    cy.visitAndLoadProject(projectIdentifier);
    cy.stopAllSessionsForProject(projectIdentifier);

    // Start a session with options
    let serversInvoked = false;
    cy.intercept("/ui-server/api/notebooks/servers*", (req) => {
      serversInvoked = true;
    }).as("getServers");
    if (projectTestConfig.shouldCreateProject) {
      cy.getDataCy("project-overview-content")
        .contains("your new Renku project", { timeout: TIMEOUTS.long })
        .should("exist");
    }
    cy.getProjectSection("Sessions").click();
    if (serversInvoked) cy.wait("@getServers");
    cy.getProjectPageLink(projectIdentifier, "sessions/new")
      .should("be.visible")
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
    cy.getDataCy("open-session").should("exist").click();
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
    });

    // Save the changes
    cy.getDataCy("save-session-button").should("be.visible").click();
    cy.get(".modal-session").contains("1 commit will be pushed").should("be.visible");
    cy.getDataCy("save-session-modal-button").should("be.visible").click();
    cy.get(".modal").contains("Saving Session", { timeout: TIMEOUTS.long }).should("be.visible");
    cy.get(".modal").contains("There are no changes", { timeout: TIMEOUTS.long }).should("be.visible");
    cy.get(".modal .btn-close").should("be.visible").click();

    // Stop the session
    cy.stopSession();

    // Be sure the project have been indexed
    cy.getDataCy("go-back-button").click();
    cy.getProjectSection("Settings").click();
    cy.getDataCy("kg-status-section-open").should("exist").click();
    cy.getDataCy("project-settings-knowledge-graph")
      .contains("Everything indexed", { timeout: TIMEOUTS.vlong })
      .should("exist");

    // Go the workflows page and check the new workflow appears
    cy.getProjectSection("Workflows").click();
    cy.getDataCy("workflows-browser")
      .should("be.visible")
      .children()
      .should("have.length", 1)
      .contains(workflow.name)
      .should("be.visible")
      .click();
    cy.getDataCy("workflow-details")
      .should("be.visible")
      .contains(`echo 123 > ${workflow.output}`)
      .should("be.visible");

    // Go the file page and check the lineage exists
    cy.getProjectSection("Files").click();
    cy.get("div.tree-container").contains("button", "Lineage").should("be.visible").click();
    cy.get("#tree-content").contains(workflow.output).should("exist").click();
    cy.get(".graphContainer").contains(workflow.output).should("exist");
  });

  it("Start a new session as anonymous user.", () => {
    // Log out and go to the project again
    cy.visit("/");
    cy.logout();
    cy.visitAndLoadProject(projectIdentifier);

    // Check we show the appropriate message
    cy.getProjectSection("Sessions").click();
    cy.getProjectPageLink(projectIdentifier, "sessions/new")
      .should("be.visible")
      .first()
      .click();
    cy.get(".alert-info").contains("As an anonymous user").should("be.visible");

    // Quickstart a session and check it spins up
    cy.getDataCy("go-back-button").click();
    cy.quickstartSession();

    // Stop the session
    cy.stopSession();
  });

  it("Start a new session on a project without permissions.", () => {
    // Log out and go to the project again
    cy.visitAndLoadProject(projectWithoutPermissions);

    // Check we show the appropriate message
    cy.getProjectSection("Sessions").click();
    cy.getProjectPageLink(projectWithoutPermissions, "sessions/new")
      .should("be.visible")
      .first()
      .click();
    cy.get(".alert-info").contains("You have limited permissions for this project").should("be.visible");

    // Quickstart a session and check it spins up
    cy.getDataCy("go-back-button").click();
    cy.quickstartSession();

    // Stop the session
    cy.stopSession();
  });
});
