export type ProjectIdentifierV2 = {
  slug: string;
  namespace?: string;
  id?: string;
};

interface NewProjectV2Props extends ProjectIdentifierV2 {
  visibility?: "public" | "private";
  name: string;
}

/** Get the namespace of the logged in user from the API. */
function getUserNamespaceAPIV2(): Cypress.Chainable<string | null> {
  return cy.request({ failOnStatusCode: false, method: "GET", url: `api/data/namespaces?minimum_role=owner` })
    .then((response) => {
      if (response.status === 200) {
        const userNamespace = response.body?.filter((namespace) => namespace.namespace_kind === "user");
        return userNamespace && userNamespace.length > 0 ? userNamespace[0].slug : null;
      }
      return null;
    });
}

/** Get a project by using only the API. */
function getProjectByNamespaceAPIV2(newProjectProps: ProjectIdentifierV2): Cypress.Chainable<any | null> {
  return cy.request({ failOnStatusCode: false, method: "GET", url: `api/data/projects/${newProjectProps.namespace}/${newProjectProps.slug}` });
}

/** Create a project (if the project is missing) by using only the API. */
function createProjectIfMissingAPIV2(newProjectProps: NewProjectV2Props) {
  return getProjectByNamespaceAPIV2(newProjectProps)
    .then((response) => {
      if (response.status != 200) {
        return cy.request({
          method: "POST",
          url: "api/data/projects",
          body: newProjectProps,
          headers: {
            'Content-Type': 'application/json'
          }
        });
      } else {
        return response.body;
      }
  });
}

/** Delete a project by using only the API. */
function deleteProjectFromAPIV2(projectIdentifier: ProjectIdentifierV2) {
  return cy.request({
    failOnStatusCode: false,
    method: "DELETE",
    url: `api/data/projects/${projectIdentifier.id}`,
  });
}

export default function registerProjectV2Commands() {
  Cypress.Commands.add("createProjectIfMissingAPIV2", createProjectIfMissingAPIV2);
  Cypress.Commands.add("deleteProjectFromAPIV2", deleteProjectFromAPIV2);
  Cypress.Commands.add("getUserNamespaceAPIV2", getUserNamespaceAPIV2);
  Cypress.Commands.add("getProjectByNamespaceAPIV2", getProjectByNamespaceAPIV2);
}

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      createProjectIfMissingAPIV2(newProjectProps: NewProjectV2Props);
      deleteProjectFromAPIV2(identifier: ProjectIdentifierV2);
      getUserNamespaceAPIV2();
      getProjectByNamespaceAPIV2(identifier: ProjectIdentifierV2);
    }
  }
}
