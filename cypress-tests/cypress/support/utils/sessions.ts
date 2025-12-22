import { TIMEOUTS } from "../../../config";
import {
  Environment,
  Session,
  SessionLauncher,
  SessionSecretSlot,
} from "../types/sessions";

export function getSessions(): Cypress.Chainable<Cypress.Response<Session[]>> {
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

/** Get a global environment by a part of its name (case-insensitive). */
export function getEnvironmentByName(
  name: string,
): Cypress.Chainable<Environment> {
  return getEnvironments().then((response) => {
    expect(response.status).to.equal(200);
    const environment = response.body.find((e) =>
      e.name.toLowerCase().includes(name.toLowerCase()),
    );
    if (!environment) {
      throw new Error(
        `No global environment found with name containing "${name}"`,
      );
    }
    return environment;
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
      response.body.forEach((session: Session) => {
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
      args: environment.args,
      command: environment.command,
      container_image: environment.container_image,
      default_url: environment.default_url ?? "/lab",
      description: environment.description,
      environment_image_source: "image",
      environment_kind: "CUSTOM",
      gid: environment.gid,
      mount_directory: environment.mount_directory,
      name: `${name}-environment`,
      port: environment.port,
      uid: environment.uid,
      working_directory: environment.working_directory,
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

export function createSessionSecretSlot(
  projectId: string,
  name: string,
  filename?: string,
  description?: string,
): Cypress.Chainable<Cypress.Response<SessionSecretSlot>> {
  return cy.request({
    method: "POST",
    url: "api/data/session_secret_slots",
    body: {
      project_id: projectId,
      name: name,
      filename: filename || name,
      description: description,
    },
  });
}

/** Get the Kubernetes namespace for sessions. */
export function getK8sNamespace(url: string): string {
  const envNamespace = Cypress.env("RENKU_NAMESPACE");
  if (envNamespace) {
    return envNamespace;
  }

  // Fall back to extracting from URL.
  // URL format: https://{namespace}.dev.renku.ch/p/{userNamespace}/{projectSlug}/sessions/show/{sessionName}
  const urlNamespace = new URL(url).hostname.split(".")[0];

  // If empty, use "renku" as default
  return urlNamespace || "renku";
}

/** Extract the pod name from a session URL. */
export function getPodName(url: string): string {
  // URL format: https://{namespace}.dev.renku.ch/p/{userNamespace}/{projectSlug}/sessions/show/{sessionName}
  const sessionName = url.split("/sessions/show/")[1];
  // Pod name format: {sessionName}-0
  return `${sessionName}-0`;
}

/** Execute a command in a running session pod using kubectl. */
export function executeInSession(
  url: string,
  command: string,
): Cypress.Chainable<string> {
  const namespace = getK8sNamespace(url);
  const podName = getPodName(url);

  return cy
    .exec(`kubectl exec --namespace ${namespace} ${podName} -- ${command}`, {
      timeout: TIMEOUTS.long,
    })
    .then((result) => result.stdout.trim());
}
