import { v4 as uuidv4 } from "uuid";

import { TIMEOUTS } from "../../../config";

// Helper functions

export type ProjectIdentifier = {
  name: string;
  namespace?: string;
};

export function generatorProjectName(name: string): string {
  if (!name) throw new Error("Project name cannot be empty");
  return `cypress-${name.toLowerCase()}-${uuidv4().substring(24)}`;
}

export function fullProjectIdentifier(
  identifier: ProjectIdentifier
): Required<ProjectIdentifier> {
  return { namespace: Cypress.env("TEST_USERNAME"), ...identifier };
}

export function projectUrlFromIdentifier(id: ProjectIdentifier) {
  return `/projects/${id.namespace}/${id.name}`;
}

export function projectPageLinkSelector(
  identifier: ProjectIdentifier,
  subpage: string
) {
  const subpageUrl = projectSubpageUrl(identifier, subpage);
  return `a[href='${subpageUrl}']`;
}

function getProjectPageLink(identifier: ProjectIdentifier, subpage: string) {
  const selector = projectPageLinkSelector(identifier, subpage);
  return cy.get(selector).should("exist");
}

function projectSubpageUrl(identifier: ProjectIdentifier, subpage: string) {
  const projectUrl = projectUrlFromIdentifier(
    fullProjectIdentifier(identifier)
  );
  const subPath = subpage.startsWith("/") ? subpage : `/${subpage}`;
  return `${projectUrl}${subPath}`;
}

function searchForProject(props: ProjectIdentifier, shouldExist = true) {
  cy.visit("/search");
  cy.get("input[placeholder='Search...']").should("exist").scrollIntoView();
  cy.get("input[placeholder='Search...']")
    .should("be.visible")
    .type(props.name)
    .type("{enter}");
  if (shouldExist) {
    cy.get("[data-cy='list-card-title']")
      .contains(props.name)
      .should("exist")
      .scrollIntoView()
      .should("be.visible");
  } else {
    cy.get(props.name).should("not.exist");
  }
}

interface NewProjectProps extends ProjectIdentifier {
  templateName?: string;
  visibility?: "public" | "private" | "internal";
}

function createProject(newProjectProps: NewProjectProps) {
  const namespace = newProjectProps.namespace ?? Cypress.env("TEST_USERNAME");
  const slug = encodeURIComponent(`${namespace}/${newProjectProps.name}`);
  cy.request({failOnStatusCode: false, method: "GET", url: `/ui-server/api/projects/${slug}`}).then((response) => {
    if (response.status != 200) {
      cy.visit("/projects/new");
      cy.getDataCy("field-group-title")
        .should("be.visible")
        .clear()
        .type(newProjectProps.name);
      if (newProjectProps.namespace) {
        cy.get("#namespace-input")
          .should("be.visible")
          .clear()
          .type(newProjectProps.namespace);
      }

      if (newProjectProps.templateName)
        cy.contains(newProjectProps.templateName).should("be.visible").click();

      if (newProjectProps.visibility) {
        cy.getDataCy(`visibility-${newProjectProps.visibility}`)
          .should("be.visible")
          .click();
      }
      // The button may take some time before it is clickable
      cy.get("[data-cy=create-project-button]", { timeout: TIMEOUTS.vlong })
        .should("be.enabled")
        .click();
    } else {
      cy.visit(`projects/${namespace}/${newProjectProps.name}`);
    }
    cy.url({ timeout: TIMEOUTS.vlong }).should(
      "contain",
      newProjectProps.name.toLowerCase()
    );
    cy.get(`[data-cy="header-project"]`, { timeout: TIMEOUTS.vlong }).should(
      "be.visible"
    );
    cy.get("ul.nav-pills-underline").should("be.visible");
  });
}

function deleteProjectFromAPI(identifier: ProjectIdentifier) {
  const id = fullProjectIdentifier(identifier);
  cy.request({
    failOnStatusCode: false,
    method: "DELETE",
    url: `/ui-server/api/kg/projects/${id.namespace}/${id.name}`,
  });
}

function deleteProject(
  identifier: ProjectIdentifier,
  navigateToProject = false
) {
  if (navigateToProject) cy.visitAndLoadProject(identifier);

  const id = fullProjectIdentifier(identifier);
  cy.request({
    failOnStatusCode: false,
    method: "DELETE",
    url: `/ui-server/api/projects/${id.namespace}%2F${id.name}`,
  });

  const slug = identifier.namespace + "/" + identifier.name;
  cy.intercept("DELETE", `/ui-server/api/kg/projects/${slug}`).as(
    "deleteProject"
  );

  // Delete the project
  cy.getProjectSection("Settings").click();
  cy.getDataCy("project-settings-general-delete-project")
    .should("be.visible")
    .find("button")
    .contains("Delete project")
    .should("be.visible")
    .click();
  cy.contains("Are you absolutely sure?");
  cy.get("input[name=project-settings-general-delete-confirm-box]").type(slug);
  cy.get("button")
    .contains("Yes, delete this project")
    .should("be.visible")
    .should("be.enabled")
    .click();
  cy.wait("@deleteProject");

  cy.url().should("not.contain", `/projects/${slug}`);
  cy.get(".Toastify").contains(`Project ${slug} deleted`).should("be.visible");
}

function forkProject(identifier: ProjectIdentifier, newName: string) {
  // open the Fork project modal
  let projectsInvoked = false;
  cy.intercept("/ui-server/api/graphql", (req) => {
    projectsInvoked = true;
  }).as("getProjects");
  cy.getDataCy("project-overview-content")
    .get("#fork-project")
    .should("be.visible")
    .click();
  if (projectsInvoked) cy.wait("@getProjects", { timeout: TIMEOUTS.long });
  cy.get("#slug", { timeout: TIMEOUTS.long })
    .should("be.visible")
    .should("contain.value", identifier.name);

  // fill in the new Title name
  cy.getDataCy("field-group-title")
    .should("exist")
    .click()
    .clear()
    .type(newName);
  cy.get("#slug", { timeout: TIMEOUTS.long })
    .should("be.visible")
    .should("contain.value", newName);
  cy.get(".modal-content")
    .contains("already taken in the selected namespace")
    .should("not.exist");

  // create the project
  cy.intercept("/ui-server/api/projects/*/fork").as("forkProject");
  cy.get(".modal-footer button")
    .contains("Fork Project")
    .should("be.visible")
    .click();
  cy.get(".modal").contains("Forking the project").should("be.visible");
  cy.wait("@forkProject", { timeout: TIMEOUTS.vlong });

  // check the new project is ready
  cy.intercept("ui-server/api/renku/cache.migrations_check*").as(
    "getMigrationsCheck"
  );
  cy.wait("@getMigrationsCheck", { timeout: TIMEOUTS.long });

  cy.getDataCy("header-project").contains(newName).should("be.visible");
  cy.getDataCy("header-project").contains("forked from").should("be.visible");
  cy.getDataCy("header-project")
    .contains(identifier.namespace + "/" + identifier.name)
    .should("be.visible");
}

function visitAndLoadProject(
  identifier: ProjectIdentifier,
  skipOutdated = false
) {
  // load project and wait for the relevant resources to be loaded
  cy.intercept("/ui-server/api/user").as("getUser");
  cy.intercept("/ui-server/api/renku/*/datasets.list*").as("getDatasets");
  let versionInvoked = false;
  cy.intercept("/ui-server/api/renku/versions", (req) => {
    versionInvoked = true;
  }).as("getVersion");
  cy.intercept("/ui-server/api/projects/*").as("getProject");
  cy.visitProject(identifier);
  cy.wait("@getUser", { timeout: TIMEOUTS.long });
  cy.wait("@getProject");
  if (versionInvoked) cy.wait("@getVersion", { timeout: TIMEOUTS.long });
  if (!skipOutdated) cy.wait("@getDatasets", { timeout: TIMEOUTS.long });

  // Other elements are re-rendered at this point; waiting 1 sec helps preventing "unmounted" errors
  // eslint-disable-next-line cypress/no-unnecessary-waiting
  cy.wait(1_000);
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

type ProjectSection =
  | "Overview"
  | "Files"
  | "Datasets"
  | "Workflows"
  | "Sessions"
  | "Settings";
function getProjectSection(section: ProjectSection) {
  return cy
    .getDataCy("project-navbar", true)
    .contains("a.nav-link", section)
    .should("be.visible");
}

function waitMetadataIndexing(justTriggered = true, goToSettings = true) {
  cy.log(`function "waitMetadataIndexing": wait for KG to process everything.`);
  if (justTriggered) {
    // ? Wait 5s since KG indexing might not start immediately after the event triggering the indexing process.
    // ? The impact on the final run time should be zero since the whole indexing process takes longer than 5s anyway.
    cy.wait(TIMEOUTS.short); // eslint-disable-line cypress/no-unnecessary-waiting
  }
  if (goToSettings) cy.getProjectSection("Settings").click();
  cy.getDataCy("kg-status-section-open").should("exist").click();
  cy.getDataCy("project-settings-knowledge-graph")
    .contains("Everything indexed", { timeout: TIMEOUTS.vlong })
    .should("exist");
}

export default function registerProjectCommands() {
  Cypress.Commands.add("createProject", createProject);
  Cypress.Commands.add("deleteProject", deleteProject);
  Cypress.Commands.add("deleteProjectFromAPI", deleteProjectFromAPI);
  Cypress.Commands.add("forkProject", forkProject);
  Cypress.Commands.add("getProjectPageLink", getProjectPageLink);
  Cypress.Commands.add("getProjectSection", getProjectSection);
  Cypress.Commands.add("searchForProject", searchForProject);
  Cypress.Commands.add("visitProject", visitProject);
  Cypress.Commands.add("visitProjectPageLink", visitProjectPageLink);
  Cypress.Commands.add("visitAndLoadProject", visitAndLoadProject);
  Cypress.Commands.add("waitMetadataIndexing", waitMetadataIndexing);
}

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      createProject(newProjectProps: NewProjectProps);
      deleteProject(identifier: ProjectIdentifier, loadProject?: boolean);
      deleteProjectFromAPI(identifier: ProjectIdentifier);
      forkProject(identifier: ProjectIdentifier, newName: string);
      getProjectPageLink(identifier: ProjectIdentifier, subpage: string);
      getProjectSection(section: ProjectSection);
      searchForProject(props: ProjectIdentifier, shouldExist?: boolean);
      visitProject(identifier: ProjectIdentifier);
      visitProjectPageLink(identifier: ProjectIdentifier, subpage: string);
      visitAndLoadProject(
        identifier: ProjectIdentifier,
        skipOutdated?: boolean
      );
      waitMetadataIndexing(justTriggered?: boolean, goToSettings?: boolean);
    }
  }
}
