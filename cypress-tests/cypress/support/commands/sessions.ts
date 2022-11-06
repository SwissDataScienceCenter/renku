export const startSession = (projectNamespace: string, projectName: string) => {
  cy.visit(`/projects/${projectNamespace}/${projectName}/sessions/new`)
  cy.get('.btn-rk-green', {timeout: 300000}).should("be.visible").should("be.enabled")
  cy.get('.btn-rk-green').click()
}

export const stopSessionFromIframe = () => {
  cy.intercept({method: "DELETE", url: /.*\/api\/notebooks\/servers\/.*/, times: 1}, (req) => {
    req.continue((res) => {
      expect(res.statusCode).to.eq(204)
    })
  })
  cy.get('[data-cy="stop-session-button"]').should("be.visible").click()
  cy.get('div.modal-session').should("be.visible").should("not.be.empty")
  cy.get('[data-cy="stop-session-modal-button"]').should("be.visible").click()
}
