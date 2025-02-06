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

/** Delete a project by using only the API. */
export function deleteGroupFromAPI(slug: string) {
  return cy
    .request({
      failOnStatusCode: false,
      method: "DELETE",
      url: `api/data/groups/${slug}`,
    })
    .as("deleteGroup");
}
