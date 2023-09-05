import { TIMEOUTS } from "../../../config";

export const validateLogin = {
  validate() {
    // This returns 401 when not properly logged in
    cy.request("/ui-server/api/user");
  },
};

export const getIframe = (selector: string) => {
  // https://github.com/cypress-io/cypress-example-recipes/blob/master/examples/blogs__iframes/cypress/support/e2e.js
  cy.log("getIframeBody");
  cy.get(selector, { timeout: TIMEOUTS.long })
    .its("0.contentDocument.body")
    .should("not.be.empty");
  return cy
    .get(selector, { timeout: TIMEOUTS.long })
    .its("0.contentDocument")
    .then(cy.wrap);
};

function getDataCy(value: string, exist: true) {
  if (exist) return cy.get(`[data-cy=${value}]`).should("exist");
  return cy.get(`[data-cy=${value}]`);
}

export default function registerGeneralCommands() {
  Cypress.Commands.add("getIframe", getIframe);
  Cypress.Commands.add("getDataCy", getDataCy);
}

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      getDataCy(value: string, exist?: boolean);
      getIframe(selector: string): Chainable<unknown>;
    }
  }
}
