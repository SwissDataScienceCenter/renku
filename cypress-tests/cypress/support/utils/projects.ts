import { ProjectV2, ProjectIdentifierV2 } from "../types/projects";

/** Get a project by using only the API. */
export function getProjectByNamespace(
  projectIdentifier: ProjectIdentifierV2,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> {
  return cy.request({
    failOnStatusCode: false,
    method: "GET",
    url: `api/data/namespaces/${projectIdentifier.namespace}/projects/${projectIdentifier.slug}`,
  });
}

/** Create a project by using only the API. */
export function createProjectIfMissingV2(
  project: ProjectV2,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> {
  return getProjectByNamespace(project).then((response) => {
    if (response.status === 200) {
      return cy.wrap(response);
    }
    return cy.request({
      method: "POST",
      url: "api/data/projects",
      body: project,
      headers: {
        "Content-Type": "application/json",
      },
    });
  });
}

/** Delete a project by using only the API. */
export function deleteProject(
  projectId: string,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> {
  return cy.request({
    failOnStatusCode: false,
    method: "DELETE",
    url: `api/data/projects/${projectId}`,
  });
}

/** Delete a project by namespace and slug using only the API. */
export function deleteProjectByNamespace(
  projectIdentifier: ProjectIdentifierV2,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> {
  return getProjectByNamespace(projectIdentifier).then((response) => {
    if (response.status === 200) {
      return deleteProject(response.body.id);
    }
    return cy.wrap(response);
  });
}
