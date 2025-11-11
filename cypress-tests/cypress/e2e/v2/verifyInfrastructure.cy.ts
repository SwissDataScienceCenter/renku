/**
 * Retry invoking a Cypress request until it succeeds or fail a few times in a row.
 * @param url - target URL
 * @param service - name of the tested service
 * @param limit - maximum attempts before failing
 * @param delaySeconds - delay between attempts
 * @returns response body from the request or `null`
 */
function retryRequest(
  url: string,
  service: string,
  limit = 6,
  delaySeconds = 20,
  retries = 1,
) {
  if (retries > limit) {
    const minutes = Math.floor((limit * delaySeconds) / 60);
    throw new Error(
      `Backend service "${service}" not responding within ${minutes} minutes at URL ${url}.`,
    );
  }

  cy.request({
    url,
    failOnStatusCode: false,
  }).then((resp) => {
    if (resp.status < 400 && !resp.body.error) return resp.body;

    cy.wait(delaySeconds * 1000).then(() =>
      retryRequest(url, service, limit, delaySeconds, retries + 1),
    );
  });
  return null;
}

describe("Verify the infrastructure is ready", () => {
  it("Can interact with the backend components", () => {
    retryRequest("api/data/version", "Data services");
    retryRequest("api/auth/login", "Gateway");
    retryRequest("config.json", "UI client");

    // Data service should return a list of default resource pools
    const dataServiceUrl = "/api/data/resource_pools";
    cy.request(dataServiceUrl).then((resp) => {
      if (resp.status >= 400 || !resp.body.length)
        throw new Error("Data service endpoints not working as expected.");
    });

    // Search should return a list of items
    const searchUrl = "/api/data/search/query";
    cy.request(searchUrl).then((resp) => {
      if (resp.status >= 400 || !("items" in resp.body))
        throw new Error("Search endpoints not working as expected.");
    });

    // UI should load
    const uiUrl = `/help`;
    cy.request(uiUrl).then((resp) => {
      if (resp.status >= 400 || !resp.body.includes(`<div id="root"`))
        throw new Error("UI not showing as expected.");
    });
  });
});
