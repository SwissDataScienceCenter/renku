
/**
 * Retry invoking a Cypress request until it succeeds or fail a few times in a row.
 * @param url - target URL
 * @param service - name of the tested service
 * @param limit - maximum attempts before failing
 * @param delaySeconds - delay between attempts
 * @returns response body from the request or `null`
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
function retryRequest(url: string, service: string, limit = 10, delaySeconds = 30, retries = 1): Cypress.Response<any> {
  if (retries > limit) {
    const minutes = Math.floor(limit * delaySeconds / 60);
    throw new Error(`Backend service "${service}" not available within ${minutes} minutes at URL ${url}.`);
  }

  cy.request({
    url,
    failOnStatusCode: false
  }).then(resp => {
    if (resp.status < 400 && !resp.body.error)
      return resp.body;

    cy.wait(delaySeconds * 1000).then(() => retryRequest(url, service, limit, delaySeconds, retries + 1));
  });
  return null;
}

describe("Verify the infrastructure is ready", () => {
  it("Can interact with the backend components", () => {
    retryRequest("api/templates/licenses", "GitLab");
    retryRequest("api/renku/versions", "Core");
    retryRequest("api/notebooks/version", "Notebooks");
    retryRequest("api/kg/entities", "Graph");
    retryRequest("api/auth/login", "Gateway");
    retryRequest("ui-server/api/allows-iframe/https%3A%2F%2Fgoogle.com", "UI server");
    retryRequest("config.json", "UI client");
    cy.visit("/");
  });
});
