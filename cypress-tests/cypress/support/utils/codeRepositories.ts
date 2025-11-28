/** Add a code repository to a project by using only the API. */
export function addCodeRepository(
  projectId: string,
  repositoryUrl: string,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> {
  // First get the project to get current repositories and etag
  return cy
    .request({
      failOnStatusCode: false,
      method: "GET",
      url: `api/data/projects/${projectId}`,
    })
    .then((response) => {
      if (response.status !== 200) {
        return cy.wrap(response);
      }
      const currentRepositories = response.body.repositories || [];
      const repositories = [...currentRepositories, repositoryUrl];
      return cy.request({
        failOnStatusCode: false,
        method: "PATCH",
        url: `api/data/projects/${projectId}`,
        body: { repositories },
        headers: {
          "Content-Type": "application/json",
          "If-Match": response.body.etag || "",
        },
      });
    });
}
