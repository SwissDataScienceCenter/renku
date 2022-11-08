export const createProject = (name: string, templateName?: string, namespace?: string) => {
  cy.visit("/projects/new")
  cy.get('[data-cy="field-group-title"]').should("be.visible").clear().type(name)
  if (namespace) {
    cy.get('#namespace-input').should("be.visible").clear().type(namespace)
  }
  if (templateName) {
    cy.contains(templateName).should("be.visible").click()
  }
  cy.get('[data-cy="create-project-button"]').should("be.visible").click()
  cy.url().should("contain", name)
  cy.get('[data-cy="header-project"]').should("be.visible")
  cy.get("ul.nav-pills-underline").should("be.visible")
}

export const deleteProject = (namespace: string, name: string) => {
  cy.request("DELETE", `/ui-server/api/projects/${namespace}%2F${name}`).its("status").should("be.lessThan", 300)
}
