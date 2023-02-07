import { TIMEOUTS } from "../../../config";

// Helper functions

type ProjectIdentifier = {
  name: string
  namespace?: string,
}

function fullProjectIdentifier(identifier: ProjectIdentifier): Required<ProjectIdentifier> {
  return { namespace: Cypress.env("TEST_USERNAME"), ...identifier };
}

function projectUrlFromIdentifier(id: Required<ProjectIdentifier>) {
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
}

function createProject(newProjectProps: NewProjectProps) {
  cy.visit("/projects/new");
  cy.dataCy("field-group-title").should("be.visible").clear().type(newProjectProps.name);
  if (newProjectProps.namespace)
    cy.get("#namespace-input").should("be.visible").clear().type(newProjectProps.namespace);

  if (newProjectProps.templateName)
    cy.contains(newProjectProps.templateName).should("be.visible").click();

  // The button may take some time before it is clickable
  cy.get("[data-cy=create-project-button]", { timeout: TIMEOUTS.vlong }).should("be.enabled").click();
  cy.url({ timeout: TIMEOUTS.vlong }).should("contain", newProjectProps.name);
  cy.get(`[data-cy="header-project"]`, { timeout: TIMEOUTS.vlong }).should("be.visible");
  cy.get("ul.nav-pills-underline").should("be.visible");
}

function deleteProject(identifier: ProjectIdentifier) {
  const id = fullProjectIdentifier(identifier);
  cy.request("DELETE", `/ui-server/api/projects/${id.namespace}%2F${id.name}`).its("status").should("be.lessThan", 300);
}

function forkProject(identifier: ProjectIdentifier, newName: string) {
  // open the Fork project modal
  let projectsInvoked = false;
  cy.intercept("/ui-server/api/graphql", req => { projectsInvoked = true; }).as("getProjects");
  cy.dataCy("project-overview-content").get("#fork-project").should("be.visible").click();
  if (projectsInvoked)
    cy.wait("@getProjects", { timeout: TIMEOUTS.long });
  cy.get(".modal-content").contains("Fork project").should("be.visible");
  cy.get("#slug").should("be.visible", { timeout: TIMEOUTS.long }).should("contain.value", identifier.name);

  cy.get(".modal-content").contains("Fork project").should("be.visible");
  cy.get("#slug").should("be.visible").should("contain.value", identifier.name);

  // fill in the new Title name
  cy.dataCy("field-group-title").should("exist").click().clear().type(newName);
  cy.get("#slug").should("be.visible").should("contain.value", newName);
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
  cy.intercept("/ui-server/api/graphql").as("getProjects");
  cy.intercept("/ui-server/api/renku/*/datasets.list*").as("getDatasets");
  let versionInvoked = false;
  cy.intercept("/ui-server/api/renku/renku/version", req => { versionInvoked = true; }).as("getVersion");
  cy.visitProject(identifier);
  cy.wait("@getUser", { timeout: TIMEOUTS.long });
  cy.wait("@getProjects", { timeout: TIMEOUTS.long });
  if (versionInvoked)
    cy.wait("@getVersion", { timeout: TIMEOUTS.long });
  if (!skipOutdated)
    cy.wait("@getDatasets", { timeout: TIMEOUTS.long });
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
