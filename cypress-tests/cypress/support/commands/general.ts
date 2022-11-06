export const getIframe = (selector: string) => {
  // https://github.com/cypress-io/cypress-example-recipes/blob/master/examples/blogs__iframes/cypress/support/e2e.js
  cy.log('getIframeBody')
  cy.get(selector, {timeout: 30000})
    .its('0.contentDocument.body').should('not.be.empty')
  return cy.get(selector, {timeout: 30000})
    .its('0.contentDocument').then(cy.wrap)
}
