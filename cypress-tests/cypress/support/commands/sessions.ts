import { TIMEOUTS } from "../../../config";
import { fullProjectIdentifier } from "./projects";
import type { ProjectIdentifier } from "./projects";

function startSession(
  identifier: ProjectIdentifier,
  options?: { buildTimeout?: number }
) {
  const buildTimeout = options.buildTimeout ?? TIMEOUTS.vlong;

  const id = fullProjectIdentifier(identifier);
  cy.visit(`/projects/${id.namespace}/${id.name}/sessions/new`);
  cy.get(".renku-container button.btn-secondary", { timeout: buildTimeout })
    .contains("Start Session")
    .should("be.visible")
    .should("be.enabled")
    .click();

  cy.contains("Connecting with your session", {
    timeout: TIMEOUTS.long,
  }).should("be.visible");
  cy.contains("Connecting with your session", {
    timeout: TIMEOUTS.vlong,
  }).should("not.exist");
}

function waitForImageToBuild(identifier: ProjectIdentifier) {
  const id = fullProjectIdentifier(identifier);
  cy.visit(`/projects/${id.namespace}/${id.name}/sessions/new`);
  cy.get(".renku-container button.btn-secondary", { timeout: TIMEOUTS.vlong })
    .contains("Start Session")
    .should("be.visible")
    .should("be.enabled");
}

function stopAllSessionsForProject(
  identifier: ProjectIdentifier,
  loadPage = true
) {
  const id = fullProjectIdentifier(identifier);
  cy.intercept("/ui-server/api/notebooks/servers*").as("getSessions");
  if (loadPage) cy.visitAndLoadProject(identifier);
  cy.getProjectSection("Sessions").click();
  cy.wait("@getSessions").then(({ response }) => {
    const servers = response?.body?.servers ?? {};
    for (const key of Object.keys(servers)) {
      // Skip any unrelated sessions
      if (
        servers[key].annotations["renku.io/namespace"] !== id.namespace ||
        servers[key].annotations["renku.io/projectName"] !== id.name
      )
        continue;

      // Stop any existing session
      cy.getDataCy("session-container")
        .find("[data-cy=more-menu]")
        .first()
        .should("be.visible")
        .click();
      cy.getDataCy("session-container")
        .find("[data-cy=delete-session-button]")
        .first()
        .should("be.visible")
        .click();
      cy.getDataCy("delete-session-modal-button").should("be.visible").click();
    }
  });
  cy.contains("No currently running sessions.", { timeout: TIMEOUTS.vlong });
}

function deleteSession(args?: { fromSessionPage?: boolean }) {
  const fromSessionPage = args?.fromSessionPage ?? false;

  if (fromSessionPage) {
    cy.getDataCy("delete-session-button").first().should("be.visible").click();
  } else {
    cy.wait(TIMEOUTS.minimal); // eslint-disable-line cypress/no-unnecessary-waiting
    cy.getDataCy("session-container")
      .find("[data-cy=more-menu]")
      .first()
      .should("be.visible")
      .click();
    cy.getDataCy("session-container")
      .find("[data-cy=delete-session-button]")
      .first()
      .should("be.visible")
      .click();
  }

  cy.getDataCy("delete-session-modal-button").should("be.visible").click();
  cy.getDataCy("stopping-btn").should("be.visible");
  cy.get(".renku-container", { timeout: TIMEOUTS.vlong })
    .contains("No currently running sessions")
    .should("be.visible");
}

function pauseSession() {
  cy.getDataCy("pause-session-button").first().should("be.visible").click();
  cy.getDataCy("pause-session-modal-button").should("be.visible").click();
  cy.get(`[data-cy="session-container"]`, { timeout: TIMEOUTS.long })
    .should("be.visible")
    .contains("Paused");
}

function quickstartSession() {
  cy.get(".start-session-button").should("not.be.disabled").click();
  cy.get(".progress-box .progress-title").should("exist");
  cy.get(".progress-box .progress-title")
    .contains("Starting Session")
    .should("exist");
  cy.get(".progress-box .progress-title", { timeout: TIMEOUTS.vlong }).should(
    "not.exist"
  );
  cy.getIframe("iframe#session-iframe").within(() => {
    cy.get(".jp-Launcher-content", { timeout: TIMEOUTS.long }).should(
      "be.visible"
    );
    cy.get(".jp-Launcher-section").should("be.visible");
    cy.get('.jp-LauncherCard[title="Start a new terminal session"]').should(
      "be.visible"
    );
  });
}

export default function registerSessionCommands() {
  Cypress.Commands.add("quickstartSession", quickstartSession);
  Cypress.Commands.add("startSession", startSession);
  Cypress.Commands.add("waitForImageToBuild", waitForImageToBuild);
  Cypress.Commands.add("deleteSession", deleteSession);
  Cypress.Commands.add("pauseSession", pauseSession);
  Cypress.Commands.add("stopAllSessionsForProject", stopAllSessionsForProject);
}

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      startSession: typeof startSession;
      waitForImageToBuild: typeof waitForImageToBuild;
      deleteSession: typeof deleteSession;
      pauseSession: typeof pauseSession;
      quickstartSession: typeof quickstartSession;
      stopAllSessionsForProject: typeof stopAllSessionsForProject;
    }
  }
}
