import { Project } from "../types/projects";

/** Add a code repository to a project. */
export function createCodeRepository(
  repositoryUrl: string,
  projectId: string,
): Cypress.Chainable<Cypress.Response<Project>> {
  // First get the project to get current repositories and etag
  return cy
    .request({
      method: "GET",
      url: `api/data/projects/${projectId}`,
    })
    .then((response) => {
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

/** Delete all code repositories from a project. */
export function deleteCodeRepositoriesForProject(projectId: string): void {
  cy.request({
    method: "GET",
    url: `api/data/projects/${projectId}`,
  }).then((response) => {
    cy.request({
      failOnStatusCode: false,
      method: "PATCH",
      url: `api/data/projects/${projectId}`,
      body: { repositories: [] },
      headers: {
        "Content-Type": "application/json",
        "If-Match": response.body.etag || "",
      },
    });
  });
}
