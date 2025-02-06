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
  limit = 10,
  delaySeconds = 30,
  retries = 1,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Response<any> {
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
    retryRequest("api/templates/licenses", "GitLab");
    retryRequest("api/renku/versions", "Core basic");
    retryRequest("api/notebooks/version", "Notebooks");
    retryRequest("api/kg/entities", "Graph");
    retryRequest("api/data/version", "Data Service");
    retryRequest("api/auth/login", "Gateway");
    retryRequest(
      `ui-server/api/allows-iframe/${encodeURIComponent("https://google.com")}`,
      "UI server",
    );
    retryRequest("config.json", "UI client");

    // ? More thorough check of the backend components
    // GitLab should return some projects
    const gitlabUrl = "/ui-server/api/projects";
    cy.request(gitlabUrl).then((resp) => {
      if (resp.status >= 400 || !resp.body.length)
        throw new Error("GitLab not working as expected.");
    });

    // Core should read the config file of a Renku project
    const gitUrl =
      "https://gitlab.dev.renku.ch/renku-ui-tests/renku-project-v10";
    const coreUrl = `/ui-server/api/renku/config.show?git_url=${encodeURIComponent(
      gitUrl,
    )}`;
    cy.request(coreUrl).then((resp) => {
      if (resp.status >= 400 || !("result" in resp.body))
        throw new Error("Core backend not working as expected.");
    });

    // Notebooks should return an empty list of servers
    const notebooksUrl = "/ui-server/api/notebooks/servers";
    cy.request(notebooksUrl).then((resp) => {
      if (resp.status >= 400 || !("servers" in resp.body))
        throw new Error("Notebooks backend not working as expected.");
    });

    // Graph should return an empty list of entities for a weird search
    const query = "nonExistingLongWordThatShouldReturnEmpty".toLowerCase();
    const graphUrl = `/ui-server/api/kg/entities?query=${query}`;
    cy.request(graphUrl).then((resp) => {
      if (resp.status >= 400 || resp.body.length !== 0)
        throw new Error("Graph backend not working as expected.");
    });

    // Data service should return a list of default resopurce pools
    const dataServiceUrl = "/ui-server/api/data/resource_pools";
    cy.request(dataServiceUrl).then((resp) => {
      if (resp.status >= 400 || !resp.body.length)
        throw new Error("GitLab not working as expected.");
    });
  });
});
