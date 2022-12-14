
import { TIMEOUTS } from "../../../config";
import { fullProjectIdentifier } from "./projects";
import type { ProjectIdentifier } from "./projects";

function startSession(identifier: ProjectIdentifier) {
  const id = fullProjectIdentifier(identifier);
  cy.visit(`/projects/${id.namespace}/${id.name}/sessions/new`);
  cy.get(".btn-rk-green", { timeout: TIMEOUTS.long }).should("be.visible").should("be.enabled").click();

  // Waits for the session to fully start
  cy.get(`.fullscreen-back-button`).should("be.visible").click();
  cy.get(".time-caption", { timeout: TIMEOUTS.vlong }).should("contain.text", "Running");

}

function waitForImageToBuild(identifier: ProjectIdentifier) {
  const id = fullProjectIdentifier(identifier);
  cy.visit(`/projects/${id.namespace}/${id.name}/sessions/new`);
  cy.get(".btn-rk-green", { timeout: TIMEOUTS.vlong }).should("be.visible").should("be.enabled");
}

export const stopSessionFromIframe = () => {
  cy.intercept({ method: "DELETE", url: /.*\/api\/notebooks\/servers\/.*/, times: 1 }, (req) => {
    req.continue((res) => {
      expect(res.statusCode).to.eq(204);
    });
  });
  cy.get(`[data-cy="stop-session-button"]`).should("be.visible").click();
  cy.get("div.modal-session").should("be.visible").should("not.be.empty");
  cy.get(`[data-cy="stop-session-modal-button"]`).should("be.visible").click();
};

export default function registerSessionCommands() {
  Cypress.Commands.add("startSession", startSession);
  Cypress.Commands.add("waitForImageToBuild", waitForImageToBuild);
  Cypress.Commands.add("stopSessionFromIframe", stopSessionFromIframe);
}


declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      startSession(identifier: ProjectIdentifier);
      waitForImageToBuild(identifier: ProjectIdentifier);
      stopSessionFromIframe();
    }
  }
}
