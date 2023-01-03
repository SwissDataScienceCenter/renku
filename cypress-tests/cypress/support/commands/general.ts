import { TIMEOUTS } from "../../../config";

export const getIframe = (selector: string) => {
  // https://github.com/cypress-io/cypress-example-recipes/blob/master/examples/blogs__iframes/cypress/support/e2e.js
  cy.log("getIframeBody");
  cy.get(selector, { timeout: TIMEOUTS.long })
    .its("0.contentDocument.body").should("not.be.empty");
  return cy.get(selector, { timeout: TIMEOUTS.long })
    .its("0.contentDocument").then(cy.wrap);
};

export type SearchForProjectProps = {
  name: string,
  namespace: string
}
export function searchForProject(props: SearchForProjectProps) {
  cy.visit("/search");
  cy.get("input[placeholder='Search...']").type(props.name).type("{enter}");
  cy.get("[data-cy='list-card-title']").contains(props.name);
}

function dataCy(value: string) {
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
      dataCy(value: string);
      getIframe(selector: string): Chainable<unknown>;
      searchForProject(props: SearchForProjectProps);
    }
  }
}
