import { Group } from "../types/groups";

export function getGroup(
  slug: string,
): Cypress.Chainable<Cypress.Response<Group>> {
  return cy.request({
    failOnStatusCode: false,
    method: "GET",
    url: `api/data/groups/${slug}`,
  });
}

export function deleteGroup(slug: string | null): void {
  if (slug) {
    cy.request({
      failOnStatusCode: false,
      method: "DELETE",
      url: `api/data/groups/${slug}`,
    });
  }
}

export function createGroupIfMissing(
  group: Group,
): Cypress.Chainable<Cypress.Response<Group>> {
  return getGroup(group.slug).then((response) => {
    if (response.status === 200) {
      return cy.wrap(response);
    }
    return cy.request({
      method: "POST",
      url: "api/data/groups",
      body: group,
      headers: {
        "Content-Type": "application/json",
      },
    });
  });
}
