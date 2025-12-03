// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function getSessions(): Cypress.Chainable<Cypress.Response<any>> {
  return cy.request({
    failOnStatusCode: false,
    method: "GET",
    url: `api/data/sessions`,
  });
}

export function deleteSessionFromAPI(
  sessionName: string,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> {
  return cy
    .request({
      failOnStatusCode: false,
      method: "DELETE",
      url: `api/data/sessions/${sessionName}`,
    })
    .as("deleteSession");
}
