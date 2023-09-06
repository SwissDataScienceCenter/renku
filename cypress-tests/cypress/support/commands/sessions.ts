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
      // if (servers[key].annotation)
      console.warn({ id });
      console.warn({ servers });

      if (
        servers[key].annotations["renku.io/namespace"] !== id.namespace ||
        servers[key].annotations["renku.io/projectName"] !== id.name
      ) {
        console.warn("Skipping", key);
        continue;
      }

      const name = servers[key].name;
      // eslint-disable-next-line cypress/no-assigning-return-values
      const connectButton = cy
        .dataCy("session-container")
        .get(`[data-cy=open-session][href$=${name}]`)
        .first()
        .should("exist");
      connectButton.siblings("[data-cy=more-menu]").click();
      connectButton
        .siblings(".dropdown-menu")
        .find("button")
        .contains("Delete session")
        .click();
      cy.dataCy("delete-session-modal-button").should("be.visible").click();
    }
  });
  cy.contains("No currently running sessions.", { timeout: TIMEOUTS.long });
  cy.get(".nav-link").contains("Overview").click();
};

export const stopSessionFromIframe = () => {
  cy.intercept(
    { method: "DELETE", url: /.*\/api\/notebooks\/servers\/.*/, times: 1 },
    (req) => {
      req.continue((res) => {
        expect(res.statusCode).to.eq(204);
      });
    }
  );
  cy.get(`[data-cy="stop-session-button"]`).should("be.visible").click();
  cy.get("div.modal-session").should("be.visible").should("not.be.empty");
  cy.get(`[data-cy="stop-session-modal-button"]`).should("be.visible").click();
  cy.contains("Stopping...", { timeout: TIMEOUTS.long }).should("be.visible");
};

export default function registerSessionCommands() {
  Cypress.Commands.add("startSession", startSession);
  Cypress.Commands.add("waitForImageToBuild", waitForImageToBuild);
  Cypress.Commands.add("stopSessionFromIframe", stopSessionFromIframe);
  Cypress.Commands.add("stopAllSessionsForProject", stopAllSessionsForProject);
}

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      startSession(identifier: ProjectIdentifier);
      waitForImageToBuild(identifier: ProjectIdentifier);
      stopSessionFromIframe();
      stopAllSessionsForProject: typeof stopAllSessionsForProject;
    }
  }
}
