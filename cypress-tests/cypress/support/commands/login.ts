export const renkuLogin = (credentials: {username: string, password: string}[]) => {
  for (const {username, password} of credentials) {
    cy.get("#username").type(username)
    cy.get("#password").type(password, {log: false})
    cy.get('#kc-login').click().should("not.exist")
  }
  cy.url().then((url) => {
    const parsedUrl = new URL(url)
    if (parsedUrl.pathname.includes("gitlab") || parsedUrl.host.includes("gitlab")) {
      cy.get('.doorkeeper-authorize >>>> .btn-danger').should("be.visible").should("be.enabled").click()
    }
  })
}

export const register = (email: string, password: string, firstName?: string, lastName?: string) => {
  cy.visit("/login")
  cy.get("div#kc-registration").find("a").should("be.visible").click()
  cy.get('input[name="firstName"]').should("be.visible").click().clear().type(firstName ? firstName : "Renku Cypress")
  cy.get('input[name="lastName"]').should("be.visible").click().clear().type(lastName ? lastName : "Test")
  cy.get('input[name="email"]').should("be.visible").click().clear().type(email)
  cy.get('input[name="password"]').should("be.visible").click().clear().type(password, {log: false})
  cy.get('input[name="password-confirm"]').should("be.visible").click().clear().type(password, {log: false})
  cy.get('input[type="submit"]').should("be.visible").should("be.enabled").click()
}
