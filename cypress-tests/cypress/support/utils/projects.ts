import { Project } from "../types/projects";

export function getProjectByNamespaceSlug(
  namespace: string,
  slug: string,
): Cypress.Chainable<Cypress.Response<Project>> {
  return cy.request({
    failOnStatusCode: false,
    method: "GET",
    url: `api/data/namespaces/${namespace}/projects/${slug}`,
  });
}

export function createProjectIfMissingV2(
  project: Project,
): Cypress.Chainable<Cypress.Response<Project>> {
  return getProjectByNamespaceSlug(project.namespace, project.slug).then(
    (response) => {
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
    },
  );
}

export function deleteProject(projectId: string | null): void {
  if (projectId) {
    cy.request({
      failOnStatusCode: false,
      method: "DELETE",
      url: `api/data/projects/${projectId}`,
    });
  }
}

export function deleteProjectByNamespaceSlug(
  namespace: string,
  slug: string,
): void {
  getProjectByNamespaceSlug(namespace, slug).then((response) => {
    if (response.status === 200) {
      deleteProject(response.body.id);
    }
  });
}
