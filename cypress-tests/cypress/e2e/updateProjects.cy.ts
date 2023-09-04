import { TIMEOUTS } from "../../config";
import { generatorProjectName } from "../support/commands/projects";
import { validateLogin } from "../support/commands/general";

const username = Cypress.env("TEST_USERNAME");

const projects = {
  shouldFork: true,
  namespace: "renku-ui-tests",
  v8: "renku-project-v8",
  v9: "renku-project-v9",
};

// ? to simplify debugging, you can change `shouldFork` to false to use the projects directly instead of forking.
// projects.shouldFork = false;
// projects.namespace = "yourNamespace";
// projects.v8 = "anotherProjectV8";
// projects.v9 = "anotherProjectV9";

describe("Fork and update old projects", () => {
  before(() => {
    // Use a session to preserve login data
    cy.session(
      "login-updateProjects",
      () => {
        cy.robustLogin();
      },
      validateLogin
    );
  });

  beforeEach(() => {
    // Restore the session
    cy.session(
      "login-updateProjects",
      () => {
        cy.robustLogin();
      },
      validateLogin
    );
  });

  it("Update a very old project - version and template", () => {
    // fork the project
    const tempName = generatorProjectName("projectUpdateV8");
    if (projects.shouldFork) {
      const forkedProject = {
        namespace: projects.namespace,
        name: projects.v8,
      };
      cy.visitAndLoadProject(forkedProject, true);
      cy.dataCy("header-project")
        .contains("Error obtaining datasets")
        .should("be.visible");
      cy.forkProject(forkedProject, tempName);
    }

    // get to the status page
    const targetProject = projects.shouldFork
      ? { namespace: username, name: tempName }
      : { namespace: projects.namespace, name: projects.v8 };
    if (!projects.shouldFork) cy.visitAndLoadProject(targetProject, true);
    cy.getProjectSection("Settings").click();

    // verify project requires update
    cy.dataCy("project-status-icon-element").should("be.visible");
    cy.dataCy("project-settings-migration-status")
      .contains("Project update required")
      .should("be.visible");
    cy.dataCy("project-version-section-open").should("be.visible").click();
    cy.dataCy("project-settings-migration-status")
      .contains("still on version 8 while the latest version is")
      .should("be.visible");
    cy.dataCy("project-settings-migration-status")
      .get("#button-update-projectMigrationStatus")
      .as("button-trigger-migration")
      .should("be.visible");
    cy.get("@button-trigger-migration").should("be.visible").click();
    cy.dataCy("project-settings-migration-status")
      .contains("button", "Updating")
      .should("be.visible");
    cy.dataCy("project-settings-migration-status")
      .contains("Refreshing project data")
      .should("be.visible");
    cy.dataCy("project-settings-migration-status")
      .contains("This project uses the latest")
      .should("be.visible");
    cy.dataCy("project-status-icon-element").should("not.exist");

    // update template
    cy.dataCy("project-settings-migration-status")
      .contains("There is a new version of the template")
      .should("be.visible");
    cy.dataCy("project-settings-migration-status")
      .get("#button-update-projectMigrationStatus")
      .as("button-update-template")
      .should("be.visible");
    cy.get("@button-update-template").should("be.visible").click();
    cy.dataCy("project-settings-migration-status")
      .contains("button", "Updating")
      .should("be.visible");
    cy.dataCy("project-settings-migration-status")
      .contains("Refreshing project data")
      .should("be.visible");
    cy.dataCy("project-settings-migration-status")
      .contains("Project up to date")
      .should("be.visible");

    // delete the project
    if (projects.shouldFork) cy.deleteProjectFromAPI(targetProject);
  });

  it("Update an outdated project - verify commits have been added", () => {
    // fork the project
    const tempName = generatorProjectName("projectUpdateV9");
    if (projects.shouldFork) {
      const forkedProject = {
        namespace: projects.namespace,
        name: projects.v9,
      };
      cy.visitAndLoadProject(forkedProject);
      cy.forkProject(forkedProject, tempName);
    }

    // get to the commits page and check there is only 1 commit
    const targetProject = projects.shouldFork
      ? { namespace: username, name: tempName }
      : { namespace: projects.namespace, name: projects.v9 };
    if (!projects.shouldFork) cy.visitAndLoadProject(targetProject);
    let commitFetched = false;
    cy.intercept(
      "/ui-server/api/projects/*/repository/commits?ref_name=master&per_page=100&page=1",
      (req) => {
        commitFetched = true;
      }
    ).as("getCommits");
    cy.dataCy("project-overview-nav").contains("a", "Commits").should("exist").click();
    if (!commitFetched) {
      cy.wait(1000); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.dataCy("refresh-commits")
        .as("refresh-commits-button-1")
        .should("be.visible");
      cy.get("@refresh-commits-button-1").should("be.visible").click();
      cy.wait(1000); // eslint-disable-line cypress/no-unnecessary-waiting
    }
    cy.wait("@getCommits", { timeout: TIMEOUTS.long });
    cy.dataCy("project-overview-content")
      .get(".card-body ul li.commit-object")
      .should("have.length", 1);

    // go to project settings and verify it requires an upodate
    cy.getProjectSection("Settings").click();
    cy.dataCy("project-status-icon-element").should("be.visible");
    cy.dataCy("project-settings-migration-status")
      .contains("Project update required")
      .should("be.visible");
    cy.dataCy("project-version-section-open").should("be.visible").click();
    cy.dataCy("project-settings-migration-status")
      .contains("still on version 9 while the latest version is")
      .should("be.visible");

    // update project
    cy.dataCy("project-settings-migration-status")
      .get("#button-update-projectMigrationStatus")
      .as("button-trigger-migration")
      .should("be.visible");
    cy.get("@button-trigger-migration").should("be.visible").click();
    cy.dataCy("project-settings-migration-status")
      .contains("button", "Updating")
      .should("be.visible");
    cy.dataCy("project-settings-migration-status")
      .contains("Refreshing project data")
      .should("be.visible");
    cy.dataCy("project-settings-migration-status")
      .contains("This project uses the latest")
      .should("be.visible");
    cy.dataCy("project-status-icon-element").should("not.exist");

    // verify the commits were added
    commitFetched = false;
    cy.getProjectSection("Overview").click();
    cy.dataCy("project-overview-nav").contains("a", "Commits").should("exist").click();
    if (!commitFetched) {
      cy.wait(1000); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.dataCy("refresh-commits")
        .as("refresh-commits-button-2")
        .should("be.visible");
      cy.get("@refresh-commits-button-2").should("be.visible").click();
      cy.wait(1000); // eslint-disable-line cypress/no-unnecessary-waiting
    }
    cy.wait("@getCommits", { timeout: TIMEOUTS.long });
    cy.dataCy("project-overview-content")
      .get(".card-body ul li.commit-object")
      .should("have.length.greaterThan", 1);
    cy.dataCy("project-overview-content")
      .get(".card-body ul li.commit-object")
      .contains("migrate to latest version")
      .should("be.visible");

    // delete the project
    if (projects.shouldFork) cy.deleteProjectFromAPI(targetProject);
  });
});
