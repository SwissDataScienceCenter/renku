import { v4 as uuidv4 } from "uuid";
import { TIMEOUTS } from "../../config";


const username = Cypress.env("TEST_USERNAME");

const projects = {
  shouldFork: true,
  namespace: "renku-ui-tests",
  v7: "renku-project-v7",
  v8: "renku-project-v8",
}

// ? to simplify debugging, you can change `shouldFork` to false to use the projects directly instead of forking.
// projects.shouldFork = false;
// projects.namespace = "yourNamespace";
// projects.v7 = "anotherProjectV7";
// projects.v8 = "anotherProjectV8";

describe("Basic public project functionality", () => {
  before(() => {
    // Save all cookies across tests
    Cypress.Cookies.defaults({
      preserve: (_) => true,
    });
    // Register and login.
    cy.robustLogin();
  });

  it("Cannot update a very old and unsupported project", () => {
    // fork the project
    const tempName = `test-project-update-v7-${uuidv4().substring(24)}`;
    if (projects.shouldFork) {
      const forkedProject = { namespace: projects.namespace, name: projects.v7 };
      cy.visitAndLoadProject(forkedProject, true);
      cy.dataCy("header-project").contains("Error obtaining datasets").should("be.visible");
      cy.forkProject(forkedProject, tempName);
    }

    // get to the status page
    const targetProject = projects.shouldFork ?
      { namespace: username, name: tempName } :
      { namespace: projects.namespace, name: projects.v7 };
    if (!projects.shouldFork)
      cy.visitAndLoadProject(targetProject, true);
    cy.getProjectPageLink(targetProject, "overview/status").should("exist").click();

    // verify project cannot be updated
    cy.dataCy("project-overview-content").contains("Project Renku Version").should("exist");
    cy.dataCy("project-overview-content").contains("(v7)").should("exist");
    cy.dataCy("project-overview-content")
      .contains("This project is not compatible with the RenkuLab UI").should("exist");

    // delete the project
    if (projects.shouldFork)
      cy.deleteProject(targetProject);
  });
});
