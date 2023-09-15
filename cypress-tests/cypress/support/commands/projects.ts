import { v4 as uuidv4 } from "uuid";

import { TIMEOUTS } from "../../../config";

// Helper functions

type ProjectIdentifier = {
  name: string
  namespace?: string,
}

export function generatorProjectName(name: string): string {
  if (!name)
    throw new Error("Project name cannot be empty");
  return `cypress-${name.toLowerCase()}-${uuidv4().substring(24)}`;
}

function fullProjectIdentifier(identifier: ProjectIdentifier): Required<ProjectIdentifier> {
  return { namespace: Cypress.env("TEST_USERNAME"), ...identifier };
}

function projectUrlFromIdentifier(id: ProjectIdentifier) {
  return `/projects/${id.namespace}/${id.name}`;
}

function projectPageLinkSelector(identifier: ProjectIdentifier, subpage: string) {
  const subpageUrl = projectSubpageUrl(identifier, subpage);
  return `a[href='${subpageUrl}']`;
}

function projectSubpageUrl(identifier: ProjectIdentifier, subpage: string) {
  const projectUrl = projectUrlFromIdentifier(fullProjectIdentifier(identifier));
  const subPath = subpage.startsWith("/") ? subpage : `/${subpage}`;
  return `${projectUrl}${subPath}`;
}


interface NewProjectProps extends ProjectIdentifier {
  templateName?: string;
  visibility?: "public" | "private" | "internal";
}

function createProject(newProjectProps: NewProjectProps) {
  cy.visit("/projects/new");
  cy.dataCy("field-group-title").should("be.visible").clear().type(newProjectProps.name);
  if (newProjectProps.namespace)
    cy.get("#namespace-input").should("be.visible").clear().type(newProjectProps.namespace);

  if (newProjectProps.templateName)
    cy.contains(newProjectProps.templateName).should("be.visible").click();

  if (newProjectProps.visibility)
    cy.dataCy(`visibility-${newProjectProps.visibility}`).should("be.visible").click();

  // The button may take some time before it is clickable
  cy.get("[data-cy=create-project-button]", { timeout: TIMEOUTS.vlong }).should("be.enabled").click();
  cy.url({ timeout: TIMEOUTS.vlong }).should("contain", newProjectProps.name.toLowerCase());
  cy.get(`[data-cy="header-project"]`, { timeout: TIMEOUTS.vlong }).should("be.visible");
  cy.get("ul.nav-pills-underline").should("be.visible");
}

function deleteProject(identifier: ProjectIdentifier) {
  const id = fullProjectIdentifier(identifier);
  cy.request({
    failOnStatusCode: false,
    method: "DELETE",
    url: `/ui-server/api/projects/${id.namespace}%2F${id.name}`
  });
}

function forkProject(identifier: ProjectIdentifier, newName: string) {
  // open the Fork project modal
  let projectsInvoked = false;
  cy.intercept("/ui-server/api/graphql", req => { projectsInvoked = true; }).as("getProjects");
  cy.dataCy("project-overview-content").get("#fork-project").should("be.visible").click();
  if (projectsInvoked)
    cy.wait("@getProjects", { timeout: TIMEOUTS.long });
  cy.get("#slug", { timeout: TIMEOUTS.long }).should("be.visible").should("contain.value", identifier.name);

  // fill in the new Title name
  cy.dataCy("field-group-title").should("exist").click().clear().type(newName);
  cy.get("#slug", { timeout: TIMEOUTS.long }).should("be.visible").should("contain.value", newName);
  cy.get(".modal-content").contains("already taken in the selected namespace").should("not.exist");

  // create the project
  cy.intercept("/ui-server/api/projects/*/fork").as("forkProject");
  cy.get(".modal-footer button").contains("Fork Project").should("be.visible").click();
  cy.get(".modal").contains("Forking the project").should("be.visible");
  cy.wait("@forkProject", { timeout: TIMEOUTS.vlong });

  // check the new project is ready
  cy.intercept("ui-server/api/renku/cache.migrations_check*").as("getMigrationsCheck");
  cy.wait("@getMigrationsCheck", { timeout: TIMEOUTS.long });

  cy.dataCy("header-project").contains(newName).should("be.visible");
  cy.dataCy("header-project").contains("forked from").should("be.visible");
  cy.dataCy("header-project").contains(identifier.namespace + "/" + identifier.name).should("be.visible");
}

function getProjectPageLink(identifier: ProjectIdentifier, subpage: string) {
  const selector = projectPageLinkSelector(identifier, subpage);
  return cy.get(selector).should("exist");
}

function visitAndLoadProject(identifier: ProjectIdentifier, skipOutdated = false) {
  // load project and wait for the relevant resources to be loaded
  cy.intercept("/ui-server/api/user").as("getUser");
  cy.intercept("/ui-server/api/renku/*/datasets.list*").as("getDatasets");
  let versionInvoked = false;
  cy.intercept("/ui-server/api/renku/versions", req => { versionInvoked = true; }).as("getVersion");
  cy.visitProject(identifier);
  cy.wait("@getUser", { timeout: TIMEOUTS.long });
  if (versionInvoked)
    cy.wait("@getVersion", { timeout: TIMEOUTS.long });
  if (!skipOutdated)
    cy.wait("@getDatasets", { timeout: TIMEOUTS.long });

  // Other elements are re-rendered at this point; waiting 1 sec helps preventing "unmounted" errors
  // eslint-disable-next-line cypress/no-unnecessary-waiting
  cy.wait(1000);
}

function visitProject(identifier: ProjectIdentifier) {
  const id = fullProjectIdentifier(identifier);
  const url = projectUrlFromIdentifier(id);
  cy.visit(url);
}

function visitProjectPageLink(identifier: ProjectIdentifier, subpage: string) {
  const url = projectSubpageUrl(identifier, subpage);
  return cy.visit(url);
}

export default function registerProjectCommands() {
  Cypress.Commands.add("createProject", createProject);
  Cypress.Commands.add("deleteProject", deleteProject);
  Cypress.Commands.add("getProjectPageLink", getProjectPageLink);
  Cypress.Commands.add("forkProject", forkProject);
  Cypress.Commands.add("visitProject", visitProject);
  Cypress.Commands.add("visitProjectPageLink", visitProjectPageLink);
  Cypress.Commands.add("visitAndLoadProject", visitAndLoadProject);
}

export { fullProjectIdentifier, projectPageLinkSelector, projectUrlFromIdentifier };
export type { ProjectIdentifier };

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      createProject(newProjectProps: NewProjectProps);
      deleteProject(identifier: ProjectIdentifier);
      forkProject(identifier: ProjectIdentifier, newName: string);
      getProjectPageLink(identifier: ProjectIdentifier, subpage: string);
      visitProject(identifier: ProjectIdentifier);
      visitProjectPageLink(identifier: ProjectIdentifier, subpage: string);
      visitAndLoadProject(identifier: ProjectIdentifier, skipOutdated?: boolean);
    }
  }
}
