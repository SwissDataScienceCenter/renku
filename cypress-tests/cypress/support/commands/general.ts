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
  cy.get("input[placeholder='Search or jump to...']").type(props.name).type("{enter}");
  cy.contains(`${props.namespace}/${props.name}`).should("be.visible");
}

export default function registerGeneralCommands() {
  Cypress.Commands.add("getIframe", getIframe);
  Cypress.Commands.add("searchForProject", searchForProject);
}

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      // general
      getIframe(selector: string): Chainable<unknown>;
      searchForProject(props: SearchForProjectProps);
    }
  }
}
