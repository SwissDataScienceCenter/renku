import { NewGroupBody } from "../types/groups";

export function getGroupFromAPI(
  slug: string,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<any | null> {
  return cy
    .request({
      failOnStatusCode: false,
      method: "GET",
      url: `api/data/groups/${slug}`,
    })
    .as("getGroup");
}

/** Delete a group by using only the API. */
export function deleteGroupFromAPI(slug: string) {
  return cy
    .request({
      failOnStatusCode: false,
      method: "DELETE",
      url: `api/data/groups/${slug}`,
    })
    .as("deleteGroup");
}

export function createGroupIfMissingAPI(newGrouptBody: NewGroupBody) {
  return getGroupFromAPI(newGrouptBody.slug).then((response) => {
    if (response.status != 200) {
      return cy.request({
        method: "POST",
        url: "api/data/groups",
        body: newGrouptBody,
        headers: {
          "Content-Type": "application/json",
        },
      });
    }
    return response.body;
  });
}
