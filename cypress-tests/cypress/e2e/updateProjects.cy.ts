import { v4 as uuidv4 } from "uuid";
import { TIMEOUTS } from "../../config";


const username = Cypress.env("TEST_USERNAME");

const projects = {
  shouldFork: true,
  namespace: "renku-ui-tests",
  v7: "renku-project-v7",
  v8: "renku-project-v8",
  v9: "renku-project-v9"
}

// ? to simplify debugging, you can change `shouldFork` to false to use the projects directly instead of forking.
// projects.shouldFork = false;
// projects.namespace = "yourNamespace";
// projects.v8 = "anotherProjectV8";
// projects.v9 = "anotherProjectV9";

describe("Fork and update old projects", () => {
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
    const tempName = `test-project-update-v8-${uuidv4().substring(24)}`;
    if (projects.shouldFork) {
      const forkedProject = { namespace: projects.namespace, name: projects.v8 };
      cy.visitAndLoadProject(forkedProject, true);
      cy.dataCy("header-project").contains("Error obtaining datasets").should("be.visible");
      cy.forkProject(forkedProject, tempName);
    }

    // get to the status page
    const targetProject = projects.shouldFork ?
      { namespace: username, name: tempName } :
      { namespace: projects.namespace, name: projects.v8 };
    if (!projects.shouldFork)
      cy.visitAndLoadProject(targetProject, true);
    cy.getProjectPageLink(targetProject, "overview/status").should("exist").click();

    // verify project cannot be updated
    cy.dataCy("project-overview-content").contains("Project Renku Version").should("exist");
    cy.dataCy("project-overview-content").contains("(v8)").should("exist");
    cy.dataCy("project-overview-content")
      .contains("This project is not compatible with the RenkuLab UI").should("exist");

    // delete the project
    if (projects.shouldFork)
      cy.deleteProject(targetProject);
  });

  it("Can update an old but still supported project", () => {
    // fork the project
    const tempName = `test-project-update-v9-${uuidv4().substring(24)}`;
    if (projects.shouldFork) {
      const forkedProject = { namespace: projects.namespace, name: projects.v9 };
      cy.visitAndLoadProject(forkedProject);
      cy.forkProject(forkedProject, tempName);
    }

    // get to the commits page and check there is only 1 commit
    const targetProject = projects.shouldFork ?
      { namespace: username, name: tempName } :
      { namespace: projects.namespace, name: projects.v9 };
    if (!projects.shouldFork)
      cy.visitAndLoadProject(targetProject);
    let commitFetched = false;
    cy.intercept(
      "/ui-server/api/projects/*/repository/commits?ref_name=master&per_page=100&page=1",
      req => { commitFetched = true; }
    ).as("getCommits");
    cy.getProjectPageLink(targetProject, "overview/commits").should("exist").click();
    if (!commitFetched) {
      cy.wait(1000) // eslint-disable-line cypress/no-unnecessary-waiting
      cy.dataCy("refresh-commits").should("exist").click();
    }
    cy.wait("@getCommits", { timeout: TIMEOUTS.long });
    cy.dataCy("project-overview-content").get(".card-body ul li.commit-object").should("have.length", 1)

    // verify project can be updated
    cy.getProjectPageLink(targetProject, "overview/status").should("exist").click();
    cy.dataCy("project-overview-content").contains("Project Renku Version").should("exist");
    cy.dataCy("project-overview-content").contains("(v9)").should("exist");
    cy.dataCy("project-overview-content")
      .contains("it is using an older version of renku").should("exist");

    // update the project
    cy.dataCy("project-overview-content").get(".alert.alert-warning button.btn-warning")
      .contains("Update").should("exist").click();
    // ? Temporarily disabled until we fix this: https://github.com/SwissDataScienceCenter/renku-ui/issues/2315
    // cy.dataCy("project-overview-content").contains("Updating...", { timeout: TIMEOUTS.long }).should("exist");
    cy.dataCy("project-overview-content")
      .contains("project is using the latest version of renku", { timeout: TIMEOUTS.vlong }).should("exist");

    // verify the commits were added
    commitFetched = false;
    cy.getProjectPageLink(targetProject, "overview/commits").should("exist").click();
    if (!commitFetched) {
      cy.wait(1000) // eslint-disable-line cypress/no-unnecessary-waiting
      cy.dataCy("refresh-commits").should("exist").click();
    }
    cy.wait("@getCommits", { timeout: TIMEOUTS.long });
    cy.dataCy("project-overview-content").get(".card-body ul li.commit-object")
      .should("have.length.greaterThan", 1)
    cy.dataCy("project-overview-content").get(".card-body ul li.commit-object")
      .contains("migrate to latest version").should("exist");

    // delete the project
    if (projects.shouldFork)
      cy.deleteProject(targetProject);
  });
});
