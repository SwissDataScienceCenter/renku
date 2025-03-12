// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function getSessionsFromAPI(): Cypress.Chainable<any | null> {
  return cy
    .request({
      failOnStatusCode: false,
      method: "GET",
      url: `api/data/sessions`,
    })
    .as("getSessions");
}

export function deleteSessionFromAPI(sessionName: string) {
  return cy
    .request({
      failOnStatusCode: false,
      method: "DELETE",
      url: `api/data/sessions/${sessionName}`,
    })
    .as("deleteSession");
}
