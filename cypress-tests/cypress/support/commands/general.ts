import { TIMEOUTS } from "../../../config";

export const validateLogin = {
  validate() {
    // If we send a request to the user endpoint on Gitlab too quickly after we log in then
    // it sometimes randomly responds with 401 and sometimes with 200 (as expected). This wait period seems to
    // allow Gitlab to "settle" after the login and properly recognize the token and respond with 200.
    cy.wait(10000);
    // This returns 401 when not properly logged in
    cy.request("ui-server/api/data/user").its("status").should("eq", 200);
    // This is how the ui decides the user is logged in
    cy.request("ui-server/api/user").then((response) => {
      expect(response.status).to.eq(200);
      expect(response.body).property("username").to.not.be.empty;
      expect(response.body).property("username").to.not.be.null;
      expect(response.body).property("state").to.equal("active");
    });
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

function getDataCy(value: string, exist?: boolean) {
  if (exist) return cy.get(`[data-cy=${value}]`).should("exist");
  return cy.get(`[data-cy=${value}]`);
}

export function getRandomString(length: number=8) {
  return Math.random().toString(20).substr(2, length)
}

export default function registerGeneralCommands() {
  Cypress.Commands.add("getIframe", getIframe);
  Cypress.Commands.add("getDataCy", getDataCy);
}

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      getDataCy: typeof getDataCy;
      getIframe: typeof getIframe;
    }
  }
}
