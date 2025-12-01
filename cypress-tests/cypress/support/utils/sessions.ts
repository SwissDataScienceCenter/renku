// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function getSessions(): Cypress.Chainable<Cypress.Response<any>> {
  return cy.request({
    failOnStatusCode: false,
    method: "GET",
    url: `api/data/sessions`,
  });
}

export function deleteSession(
  sessionName: string,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> {
  return cy.request({
    failOnStatusCode: false,
    method: "DELETE",
    url: `api/data/sessions/${sessionName}`,
  });
}

export function createSessionLauncher(
  projectId: string,
  name: string,
  containerImage: string,
  defaultUrl = "/lab",
  resourceClassId = 1,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> {
  return cy.request({
    method: "POST",
    url: "api/data/session_launchers",
    body: {
      project_id: projectId,
      name,
      resource_class_id: resourceClassId,
      environment: {
        name: `${name}-env`,
        container_image: containerImage,
        default_url: defaultUrl,
        environment_kind: "CUSTOM",
        environment_image_source: "image",
      },
    },
  });
}

export function deleteSessionLauncher(
  launcherId: string,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> {
  return cy.request({
    failOnStatusCode: false,
    method: "DELETE",
    url: `api/data/session_launchers/${launcherId}`,
  });
}

export function setSessionLauncherImage(
  launcherId: string,
  containerImage: string,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
): Cypress.Chainable<Cypress.Response<any>> {
  return cy.request({
    method: "PATCH",
    url: `api/data/session_launchers/${launcherId}`,
    body: {
      environment: {
        container_image: containerImage,
      },
    },
  });
}
