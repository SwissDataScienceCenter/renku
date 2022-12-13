import { TIMEOUTS } from "../../../config";

type ProjectIdentifier = {
  name: string
  namespace?: string,
}

function fullProjectIdentifier(identifier): Required<ProjectIdentifier> {
  return { namespace: Cypress.env("TEST_USERNAME"), ...identifier };
}
interface NewProjectProps extends ProjectIdentifier {
  templateName?: string;
}

function createProject(newProjectProps: NewProjectProps) {
  cy.visit("/projects/new");
  cy.get(`[data-cy="field-group-title"]`).should("be.visible").clear().type(newProjectProps.name);
  if (newProjectProps.namespace)
    cy.get("#namespace-input").should("be.visible").clear().type(newProjectProps.namespace);

  if (newProjectProps.templateName)
    cy.contains(newProjectProps.templateName).should("be.visible").click();

  cy.get(`[data-cy="create-project-button"]`).should("be.visible").click();
  cy.url().should("contain", name);
  cy.get(`[data-cy="header-project"]`, { timeout: TIMEOUTS.long }).should("be.visible");
  cy.get("ul.nav-pills-underline").should("be.visible");
}

function deleteProject(identifier: ProjectIdentifier) {
  const id = fullProjectIdentifier(identifier);
  cy.request("DELETE", `/ui-server/api/projects/${id.namespace}%2F${id.name}`).its("status").should("be.lessThan", 300);
}

function visitProject(identifier: ProjectIdentifier) {
  // any passed-in props should overwrite, so spread identifier last
  const id = fullProjectIdentifier(identifier);
  cy.visit(`/projects/${id.namespace}/${id.name}`);
}

export default function registerProjectCommands() {
  Cypress.Commands.add("createProject", createProject);
  Cypress.Commands.add("deleteProject", deleteProject);
  Cypress.Commands.add("visitProject", visitProject);
}

export { fullProjectIdentifier };
export type { ProjectIdentifier };

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      createProject(newProjectProps: NewProjectProps);
      deleteProject(identifier: ProjectIdentifier);
      visitProject(identifier: ProjectIdentifier)
    }
  }
}
