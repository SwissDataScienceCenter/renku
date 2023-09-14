import { TIMEOUTS } from "../../../config";
import { fullProjectIdentifier } from "./projects";
import type { ProjectIdentifier } from "./projects";

function startSession(identifier: ProjectIdentifier) {
  const id = fullProjectIdentifier(identifier);
  cy.visit(`/projects/${id.namespace}/${id.name}/sessions/new`);
  cy.get(".btn-rk-green", { timeout: TIMEOUTS.long })
    .contains("Start session")
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
  cy.get(".btn-rk-green", { timeout: TIMEOUTS.vlong })
    .should("be.visible")
    .should("be.enabled");
}

const stopAllSessionsForProject = (identifier: ProjectIdentifier) => {
  const id = fullProjectIdentifier(identifier);
  cy.intercept("/ui-server/api/notebooks/servers*").as("getSessions");
  cy.visit(`/projects/${id.namespace}/${id.name}/sessions`);
  cy.wait("@getSessions").then(({ response }) => {
    const servers = response?.body?.servers ?? {};
    for (const key of Object.keys(servers)) {
      const name = servers[key].name;
      // eslint-disable-next-line cypress/no-assigning-return-values
      const connectButton = cy
        .get(`[data-cy=open-session][href$=${name}]`)
        .should("exist");
      connectButton.siblings("[data-cy=more-menu]").click();
      connectButton
        .siblings(".dropdown-menu")
        .find("button")
        .contains("Stop")
        .click();
    }
  });
  cy.contains("No currently running sessions.", { timeout: TIMEOUTS.long });
  cy.getDataCy("go-back-button").click();
};

export const stopSession = () => {
  // Stop the session
  cy.getDataCy("stop-session-button").should("exist").click();
  cy.getDataCy("stop-session-modal-button").should("exist").click();
  cy.get(".renku-container", { timeout: TIMEOUTS.long })
    .contains("No currently running sessions.", { timeout: TIMEOUTS.long });
};

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
  Cypress.Commands.add("stopSession", stopSession);
  Cypress.Commands.add("stopAllSessionsForProject", stopAllSessionsForProject);
}

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      startSession(identifier: ProjectIdentifier);
      waitForImageToBuild(identifier: ProjectIdentifier);
      stopSession();
      quickstartSession();
      stopAllSessionsForProject: typeof stopAllSessionsForProject;
    }
  }
}
