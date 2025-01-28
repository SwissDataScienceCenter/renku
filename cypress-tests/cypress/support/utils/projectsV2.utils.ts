export type ProjectIdentifierV2 = {
  slug: string;
  namespace?: string;
  id?: string;
};

export interface NewProjectV2Props extends ProjectIdentifierV2 {
  visibility?: "public" | "private";
  name: string;
}

/** Get the namespace of the logged in user from the API. */
export function getUserNamespaceAPIV2(): Cypress.Chainable<string | null> {
  return cy
    .request({
      failOnStatusCode: false,
      method: "GET",
      url: `api/data/namespaces?minimum_role=owner`,
    })
    .then((response) => {
      if (response.status === 200) {
        const userNamespace = response.body?.filter(
          (namespace) => namespace.namespace_kind === "user",
        );
        return userNamespace && userNamespace.length > 0
          ? userNamespace[0].slug
          : null;
      }
      return null;
    });
}

/** Get a project by using only the API. */
export function getProjectByNamespaceAPIV2(
  newProjectProps: ProjectIdentifierV2,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<any | null> {
  return cy.request({
    failOnStatusCode: false,
    method: "GET",
    url: `api/data/namespaces/${newProjectProps.namespace}/projects/${newProjectProps.slug}`,
  });
}

/** Create a project (if the project is missing) by using only the API. */
export function createProjectIfMissingAPIV2(
  newProjectProps: NewProjectV2Props,
) {
  return getProjectByNamespaceAPIV2(newProjectProps).then((response) => {
    if (response.status != 200) {
      return cy.request({
        method: "POST",
        url: "api/data/projects",
        body: newProjectProps,
        headers: {
          "Content-Type": "application/json",
        },
      });
    }
    return response.body;
  });
}

/** Delete a project by using only the API. */
export function deleteProjectFromAPIV2(projectIdentifier: ProjectIdentifierV2) {
  return cy.request({
    failOnStatusCode: false,
    method: "DELETE",
    url: `api/data/projects/${projectIdentifier.id}`,
  });
}
