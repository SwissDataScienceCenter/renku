import { TIMEOUTS } from "../../config";
import { validateLogin, getRandomString } from "../support/commands/general";
import { generatorProjectName } from "../support/commands/projects";

const username = Cypress.env("TEST_USERNAME");

const projects = {
  shouldFork: true,
  namespace: "renku-ui-tests",
  v8: "renku-project-v8",
  v9: "renku-project-v9",
  v10: "renku-project-v10",
};

// ? to simplify debugging, you can change `shouldFork` to false to use the projects directly instead of forking.
// projects.shouldFork = false;
// projects.namespace = "yourNamespace";
// projects.v8 = "anotherProjectV8";
// projects.v9 = "anotherProjectV9";

const sessionId = ["updateProjects", getRandomString()];

describe("Fork and update old projects", () => {
  beforeEach(() => {
    // Restore the session
    cy.session(
      sessionId,
      () => {
        cy.robustLogin();
      },
      validateLogin,
    );
  });

  // ? This is disabled cause updating older projects now sometimes requires double migrations
  it.skip("Update a very old project - version and template", () => {
    // fork the project
    const tempName = generatorProjectName("projectUpdateV8");
    if (projects.shouldFork) {
      const forkedProject = {
        namespace: projects.namespace,
        name: projects.v8,
      };
      cy.visitAndLoadProject(forkedProject, true);
      cy.getDataCy("header-project")
        .contains("Error obtaining datasets")
        .should("be.visible");
      cy.forkProject(forkedProject, tempName);
    }

    // get to the status page
    const targetProject = projects.shouldFork
      ? { namespace: username, name: tempName }
      : { namespace: projects.namespace, name: projects.v8 };
    if (!projects.shouldFork) cy.visitAndLoadProject(targetProject, true);
    // eslint-disable-next-line cypress/no-unnecessary-waiting
    cy.wait(2_000);
    cy.getProjectSection("Settings").click();

    // verify project requires update
    cy.getDataCy("project-status-icon-element").should("be.visible");
    cy.getDataCy("project-settings-migration-status")
      .contains("Project update required")
      .should("be.visible");
    cy.getDataCy("project-version-section-open").should("be.visible").click();
    cy.getDataCy("project-settings-migration-status")
      .contains("still on version 8 while the latest version is")
      .should("be.visible");
    cy.getDataCy("project-settings-migration-status")
      .get("#button-update-projectMigrationStatus")
      .as("button-trigger-migration")
      .should("be.visible");
    cy.get("@button-trigger-migration").should("be.visible").click();
    cy.getDataCy("project-settings-migration-status")
      .contains("button", "Updating")
      .should("be.visible");
    cy.getDataCy("project-settings-migration-status")
      .contains("Refreshing project data")
      .should("be.visible");
    cy.getDataCy("project-settings-migration-status")
      .contains("This project uses the latest")
      .should("be.visible");
    cy.getDataCy("project-status-icon-element").should("not.exist");

    // update template
    cy.getDataCy("project-settings-migration-status")
      .contains("There is a new version of the template")
      .should("be.visible");
    cy.getDataCy("project-settings-migration-status")
      .get("#button-update-projectMigrationStatus")
      .as("button-update-template")
      .should("be.visible");
    cy.get("@button-update-template").should("be.visible").click();
    cy.getDataCy("project-settings-migration-status")
      .contains("button", "Updating")
      .should("be.visible");
    cy.getDataCy("project-settings-migration-status")
      .contains("Refreshing project data")
      .should("be.visible");
    cy.getDataCy("project-settings-migration-status")
      .contains("Project up to date")
      .should("be.visible");

    // delete the project
    if (projects.shouldFork) cy.deleteProjectFromAPI(targetProject);
  });

  it("Update an outdated project - verify commits have been added", () => {
    // fork the project
    const tempName = generatorProjectName("projectUpdateV10");
    if (projects.shouldFork) {
      const forkedProject = {
        namespace: projects.namespace,
        name: projects.v10,
      };
      cy.visitAndLoadProject(forkedProject);
      cy.forkProject(forkedProject, tempName);
    }

    // get to the commits page and check there is only 1 commit
    const targetProject = projects.shouldFork
      ? { namespace: username, name: tempName }
      : { namespace: projects.namespace, name: projects.v10 };
    if (!projects.shouldFork) cy.visitAndLoadProject(targetProject);
    let commitFetched = false;
    cy.intercept(
      "/ui-server/api/projects/*/repository/commits?ref_name=master&per_page=100&page=1",
      () => {
        commitFetched = true;
      },
    ).as("getCommits");
    cy.getDataCy("project-overview-nav")
      .contains("a", "Commits")
      .should("exist")
      .click();
    if (!commitFetched) {
      cy.wait(TIMEOUTS.minimal); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.getDataCy("refresh-commits")
        .as("refresh-commits-button-1")
        .should("be.visible");
      cy.get("@refresh-commits-button-1").should("be.visible").click();
      cy.wait(TIMEOUTS.minimal); // eslint-disable-line cypress/no-unnecessary-waiting
    }
    cy.wait("@getCommits", { timeout: TIMEOUTS.long });
    cy.getDataCy("project-overview-content")
      .get(".card-body ul li.commit-object")
      .should("have.length", 1);

    // go to project settings and verify it requires an update
    cy.getProjectSection("Settings").click();
    // ? this section is needed for older major versions -- swap it when needed
    // cy.getDataCy("project-status-icon-element").should("be.visible");
    // cy.getDataCy("project-settings-migration-status")
    //   .contains("Project update required")
    //   .should("be.visible");
    // cy.getDataCy("project-version-section-open").should("be.visible").click();
    // cy.getDataCy("project-settings-migration-status")
    //   .contains("still on version 9 while the latest version is")
    //   .should("be.visible");
    cy.getDataCy("project-settings-migration-status")
      .contains("Project update available")
      .should("be.visible");
    cy.getDataCy("project-version-section-open").should("be.visible").click();
    cy.getDataCy("project-settings-migration-status")
      .contains("There is a new Renku version")
      .should("be.visible");

    // update project
    cy.getDataCy("project-settings-migration-status")
      .get("#button-update-projectMigrationStatus")
      .as("button-trigger-migration")
      .should("be.visible");
    cy.get("@button-trigger-migration").should("be.visible").click();
    cy.getDataCy("project-settings-migration-status")
      .contains("button", "Updating")
      .should("be.visible");
    cy.getDataCy("project-settings-migration-status")
      .contains("Refreshing project data")
      .should("be.visible");
    cy.getDataCy("project-settings-migration-status")
      .contains("This project uses the latest")
      .should("be.visible");
    cy.getDataCy("project-status-icon-element").should("not.exist");

    // verify the commits were added
    commitFetched = false;
    cy.getProjectSection("Overview").click();
    cy.getDataCy("project-overview-nav")
      .contains("a", "Commits")
      .should("exist")
      .click();
    if (!commitFetched) {
      cy.wait(TIMEOUTS.minimal); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.getDataCy("refresh-commits")
        .as("refresh-commits-button-2")
        .should("be.visible");
      cy.get("@refresh-commits-button-2").should("be.visible").click();
      cy.wait(TIMEOUTS.minimal); // eslint-disable-line cypress/no-unnecessary-waiting
    }
    cy.wait("@getCommits", { timeout: TIMEOUTS.long });
    cy.getDataCy("project-overview-content")
      .get(".card-body ul li.commit-object")
      .should("have.length.greaterThan", 1);
    cy.getDataCy("project-overview-content")
      .get(".card-body ul li.commit-object")
      .contains("migrate to latest version")
      .should("be.visible");

    // delete the project
    if (projects.shouldFork) cy.deleteProjectFromAPI(targetProject);
  });
});
