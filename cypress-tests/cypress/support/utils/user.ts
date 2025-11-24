/** Get the namespace of the logged in user from the API. */
export function getUserNamespace(): Cypress.Chainable<string | null> {
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
