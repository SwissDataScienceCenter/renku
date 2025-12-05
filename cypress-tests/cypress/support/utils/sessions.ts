import { Environment, SessionLauncher } from "../types/sessions";

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function getSessions(): Cypress.Chainable<Cypress.Response<any>> {
  return cy.request({
    failOnStatusCode: false,
    method: "GET",
    url: `api/data/sessions`,
  });
}

export function getEnvironments(): Cypress.Chainable<
  Cypress.Response<Environment[]>
> {
  return cy.request({
    failOnStatusCode: false,
    method: "GET",
    url: `api/data/environments`,
  });
}

/** Get a global environment by a part of its name. */
export function getEnvironmentByName(
  name: string,
): Cypress.Chainable<Environment> {
  return getEnvironments().then((response) => {
    expect(response.status).to.equal(200);
    const env = response.body.find((e) =>
      e.name.toLowerCase().includes(name.toLowerCase()),
    );
    if (!env) {
      throw new Error(
        `No global environment found with name containing "${name}"`,
      );
    }
    return env;
  });
}

export function deleteSession(sessionName: string): void {
  cy.request({
    failOnStatusCode: false,
    method: "DELETE",
    url: `api/data/sessions/${sessionName}`,
  });
}

export function deleteSessionsForProject(projectId: string): void {
  getSessions().then((response) => {
    if (response.status === 200 && response.body.length > 0) {
      response.body.forEach((session: SessionLauncher) => {
        if (session.project_id === projectId) {
          deleteSession(session.name);
        }
      });
    }
  });
}

export function createSessionLauncher(
  projectId: string,
  name: string,
  environment: Environment,
  resourceClassId = 1,
): Cypress.Chainable<Cypress.Response<SessionLauncher>> {
  if ("id" in environment) {
    environment = { id: environment.id };
  } else if ("container_image" in environment) {
    environment = {
      name: `${name}-env`,
      container_image: environment.container_image,
      default_url: environment.default_url ?? "/lab",
      environment_kind: "CUSTOM",
      environment_image_source: "image",
    };
  } else {
    throw new Error(
      "Either `id` or `container_image` must be provided for the environment",
    );
  }

  return cy.request({
    method: "POST",
    url: "api/data/session_launchers",
    body: {
      project_id: projectId,
      name,
      resource_class_id: resourceClassId,
      environment: environment,
    },
  });
}

export function deleteSessionLauncher(launcherId: string): void {
  cy.request({
    failOnStatusCode: false,
    method: "DELETE",
    url: `api/data/session_launchers/${launcherId}`,
  });
}

export function setSessionLauncherEnvironment(
  launcherId: string,
  environment: Environment,
): Cypress.Chainable<Cypress.Response<SessionLauncher>> {
  if ("id" in environment) {
    environment = { id: environment.id };
  } else if ("container_image" in environment) {
    environment = {
      container_image: environment.container_image,
      environment_kind: "CUSTOM",
      environment_image_source: "image",
    };
  } else {
    throw new Error(
      "Either `id` or `container_image` must be provided for the environment",
    );
  }

  return cy.request({
    method: "PATCH",
    url: `api/data/session_launchers/${launcherId}`,
    body: {
      environment: environment,
    },
  });
}
