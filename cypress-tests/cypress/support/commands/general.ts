import { TIMEOUTS } from "../../../config";
import { ProjectIdentifier } from "./projects";

export const validateLogin = {
  validate() {
    // This returns 401 when not properly logged in
    cy.request("/ui-server/api/user");
  }
};

export const getIframe = (selector: string) => {
  // https://github.com/cypress-io/cypress-example-recipes/blob/master/examples/blogs__iframes/cypress/support/e2e.js
  cy.log("getIframeBody");
  cy.get(selector, { timeout: TIMEOUTS.long })
    .its("0.contentDocument.body").should("not.be.empty");
  return cy.get(selector, { timeout: TIMEOUTS.long })
    .its("0.contentDocument").then(cy.wrap);
};

export function searchForProject(props: ProjectIdentifier, shouldExist = true) {
  cy.visit("/search");
  cy.get("input[placeholder='Search...']").should("be.visible").type(props.name).type("{enter}");
  if (shouldExist)
    cy.get("[data-cy='list-card-title']").contains(props.name).should("be.visible");
  else
    cy.get(props.name).should("not.exist");
}

function dataCy(value: string, exist: true) {
  if (exist)
    return cy.get(`[data-cy=${value}]`).should("exist");
  return cy.get(`[data-cy=${value}]`);
}

export default function registerGeneralCommands() {
  Cypress.Commands.add("getIframe", getIframe);
  Cypress.Commands.add("searchForProject", searchForProject);
  Cypress.Commands.add("dataCy", dataCy);
}

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      dataCy(value: string, exist?: boolean);
      getIframe(selector: string): Chainable<unknown>;
      searchForProject(props: ProjectIdentifier, shouldExist?: boolean);
    }
  }
}
