import { Group } from "../types/groups";

export function getGroup(
  slug: string,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> {
  return cy.request({
    failOnStatusCode: false,
    method: "GET",
    url: `api/data/groups/${slug}`,
  });
}

/** Delete a group by using only the API. */
export function deleteGroup(
  slug: string,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> {
  return cy.request({
    failOnStatusCode: false,
    method: "DELETE",
    url: `api/data/groups/${slug}`,
  });
}

export function createGroupIfMissing(
  group: Group,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> {
  return getGroup(group.slug).then((response) => {
    if (response.status == 200) {
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
