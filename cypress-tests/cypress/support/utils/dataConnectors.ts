import { DataConnector } from "../types/dataConnectors";

/** Parse a data connector identifier to extract namespace and slug. */
function parseDataConnectorIdentifier(identifier: string): {
  namespace: string;
  slug: string;
} {
  const parts = identifier.split("/");
  const slug = parts[parts.length - 1];
  const namespace = parts.slice(0, -1).join("/");
  return { namespace, slug };
}

/** Create an S3/giab data connector. */
export function createDataConnector(
  dataConnectorOrIdentifier: DataConnector | string,
  projectId?: string,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> {
  let body: DataConnector;

  if (typeof dataConnectorOrIdentifier === "string") {
    const { namespace, slug } = parseDataConnectorIdentifier(
      dataConnectorOrIdentifier,
    );

    body = {
      name: slug,
      namespace: namespace,
      slug: slug,
      visibility: "private",
    };
  } else {
    body = dataConnectorOrIdentifier;
  }

  return cy
    .request({
      method: "POST",
      url: "api/data/data_connectors",
      body: {
        name: body.name || body.slug,
        namespace: body.namespace,
        slug: body.slug,
        visibility: body.visibility || "private",
        description: body.description || "Test data connector description",
        storage: {
          storage_type: "s3",
          configuration: {
            type: "s3",
            provider: "AWS",
            region: "us-east-1",
          },
          source_path: "giab",
          target_path: body.slug || "/",
          readonly: false,
        },
      },
      headers: {
        "Content-Type": "application/json",
      },
    })
    .then((response) => {
      if (projectId) {
        return cy
          .request({
            method: "POST",
            url: `api/data/data_connectors/${response.body.id}/project_links`,
            body: {
              project_id: projectId,
            },
            headers: {
              "Content-Type": "application/json",
            },
          })
          .then(() => {
            return cy.wrap(response);
          });
      }

      return cy.wrap(response);
    });
}

/** Get all data connectors. */
export function getAllDataConnectors(): // eslint-disable-next-line @typescript-eslint/no-explicit-any
Cypress.Chainable<Cypress.Response<any>> | Cypress.Chainable<null> {
  return cy.request({
    method: "GET",
    url: "api/data/data_connectors?per_page=100",
  });
}

/** Delete a data connector by its identifier (namespace/slug). */
export function deleteDataConnector(
  dataConnectorIdentifier: string | string[],
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> | Cypress.Chainable<null> {
  const identifiers = Array.isArray(dataConnectorIdentifier)
    ? dataConnectorIdentifier
    : [dataConnectorIdentifier];

  return getAllDataConnectors().then((response) => {
    identifiers.forEach((identifier) => {
      const { namespace, slug } = parseDataConnectorIdentifier(identifier);

      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const dataConnector = response.body.find((dc: any) => {
        return dc.namespace === namespace && dc.slug === slug;
      });

      if (dataConnector) {
        cy.request({
          method: "DELETE",
          url: `api/data/data_connectors/${dataConnector.id}`,
        });
      }
    });

    return null;
  });
}
