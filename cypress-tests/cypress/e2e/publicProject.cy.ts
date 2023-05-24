import { v4 as uuidv4 } from "uuid";
import { TIMEOUTS } from "../../config";

import { projectUrlFromIdentifier } from "../support/commands/projects";

const username = Cypress.env("TEST_USERNAME");

const projectTestConfig = {
  shouldCreateProject: true,
  projectName: `test-project-${uuidv4()}`
};

// ? Modify the config -- useful for debugging
// projectTestConfig.shouldCreateProject = false;
// projectTestConfig.projectName = "test-project-a6b94968-d1a8-435b-96f7-6a93ec5617fc";

const projectIdentifier = { name: projectTestConfig.projectName, namespace: username };

/**
 * Helper function to re-try clicking on a project page link when it detatches from the DOM
 * @param page - target project sub-page
 */
function robustNavigateToProjectPage(page: string) {
  // Requery when the element is detatched
  cy.getProjectPageLink(projectIdentifier, page).first().then(($el) =>
    Cypress.dom.isDetached($el) ?
      cy.getProjectPageLink(projectIdentifier, page) :
      $el
  ).first().click();
}

describe("Basic public project functionality", () => {
  before(() => {
    // Save all cookies across tests
    Cypress.Cookies.defaults({
      preserve: (_) => true,
    });
    // Register with the CI deployment
    cy.robustLogin();

    // Create a project
    if (projectTestConfig.shouldCreateProject) {
      cy.visit("/");
      cy.createProject({ templateName: "Basic Python", ...projectIdentifier });
    }
  });

  after(() => {
    if (projectTestConfig.shouldCreateProject)
      cy.deleteProject(projectIdentifier);
  });

  beforeEach(() => {
    cy.visitAndLoadProject(projectIdentifier);
  });

  it("Can search for project", () => {
    // assess the project has been indexed properly -- this might take time if it was recently created
    cy.dataCy("project-overview-nav").contains("Status").should("be.visible").click();
    cy.dataCy("project-overview-content")
      .contains("Knowledge Graph integration is active", { timeout: TIMEOUTS.vlong }).should("be.visible");
    // ? wait a moment to prevent "project not found" error
    cy.wait(2000); // eslint-disable-line cypress/no-unnecessary-waiting
    cy.searchForProject(projectIdentifier);

    // logout and search for the project and log back in
    cy.logout();
    cy.get("#nav-hamburger").should("be.visible").click();
    cy.searchForProject(projectIdentifier);
    cy.robustLogin();
  });

  it("Can can see overview content", () => {
    cy.contains("README.md").should("be.visible");
    cy.contains("This is a Renku project").should("be.visible");
    if (projectTestConfig.shouldCreateProject)
      cy.contains("Welcome to your new Renku project", {
        timeout: TIMEOUTS.vlong,
      }).should("be.visible");

    // Check the clone URLs
    const projectUrl = projectUrlFromIdentifier(projectIdentifier);
    // The clone url does not include "/projects" in it
    const cloneSubUrl = projectUrl.substring("/projects".length);
    cy.get("button").contains("Clone").should("be.visible").click();
    cy.contains("Clone with Renku")
      .should("be.visible")
      .next()
      .get("code")
      .contains("renku clone")
      .should("be.visible")
      .and("contain.text", cloneSubUrl);
    cy.contains("Repository SSH URL")
      .should("be.visible")
      .next()
      .get("code")
      .should("be.visible")
      .and("contain.text", cloneSubUrl);
    cy.contains("Repository HTTPS URL")
      .should("be.visible")
      .next()
      .get("code")
      .contains("https")
      .and("contain.text", cloneSubUrl);
    cy.get("button").contains("Clone").should("be.visible").click();

    cy.contains("Status").should("be.visible").click();
    // ! TODO: temporarily disabled until the new project status section is ready
    // cy.contains("This project is using the latest version of renku").should(
    //   "be.visible"
    // );
    cy.contains(
      "This project is using the latest version of the template"
    ).should("be.visible");
    cy.contains("Knowledge Graph integration is active", {
      timeout: TIMEOUTS.long,
    }).should("be.visible");
  });

  it("Can view files", () => {
    cy.contains("Files").should("exist").click();
    cy.get("div#tree-content").contains(".renku").should("exist").click();
    cy.get("div#tree-content").contains("metadata").should("exist").click();
    cy.getProjectPageLink(projectIdentifier, "/files/blob/.renku/metadata/project").click();
    cy.contains("\"@renku_data_type\": \"renku.domain_model.project.Project\"").should("exist");
    cy.get("div#tree-content").contains("README.md").should("exist").click();
    cy.contains("This is a Renku project").scrollIntoView().should("be.visible");
  });

  it("Can can work with datasets", () => {
    let migrationsInvoked = false;
    cy.intercept("/ui-server/api/renku/cache.migrations_check*", req => {
      migrationsInvoked = true;
    }).as("checkMigrations");
    const datasetName = `test-dataset-${uuidv4()}`;
    const datasetTitle = datasetName.replace("-", " ");
    cy.getProjectPageLink(projectIdentifier, "/datasets").click();
    // wait for migrations check to terminate
    if (migrationsInvoked)
      cy.wait("@checkMigrations");
    // A project we just created should have no datasets
    if (projectTestConfig.shouldCreateProject)
      cy.contains("No datasets found for this project.", { timeout: TIMEOUTS.vlong }).should("be.visible");

    // Create a dataset
    cy.get("#plus-dropdown").should("exist").click();
    cy.get("#navbar-dataset-new").should("exist").click();
    cy.dataCy("input-title").type(datasetTitle);
    cy.dataCy("input-keywords").type("test{enter}automated test{enter}");
    cy.get("div.ck.ck-editor__main div.ck.ck-content").should("exist").type("This is a test dataset");
    cy.intercept("/ui-server/api/renku/*/datasets.list?git_url=*").as("listDatasets");
    cy.dataCy("submit-button").click();
    cy.contains("Creating Dataset").should("be.visible");
    cy.wait("@listDatasets", { timeout: TIMEOUTS.vlong });

    // Check that the content is as expected
    cy.contains(datasetTitle).should("be.visible");
    cy.contains("#test").should("be.visible");
    cy.contains("#automated test").should("be.visible");
    cy.contains("This is a test dataset").should("be.visible");

    // Modify the dataset
    cy.dataCy("edit-dataset-button").last().click();
    cy.dataCy("input-keywords").type("modified{enter}");
    cy.dataCy("submit-button").click();
    cy.contains("Editing dataset").should("be.visible");
    cy.wait("@listDatasets", { timeout: TIMEOUTS.vlong });
    cy.contains("#modified").should("be.visible");

    // Check that we can see the dataset
    cy.getProjectPageLink(projectIdentifier, "/datasets").click();
    cy.dataCy("entity-description").contains("This is a test dataset").should("exist");
    cy.contains(datasetTitle).should("be.visible").click();

    // Delete the dataset
    cy.dataCy("delete-dataset-button").click();
    cy.contains("Are you sure you want to delete dataset").should("be.visible");
    cy.get(".modal").contains("Delete dataset").click();
    cy.get(".modal").contains("Deleting dataset...").should("be.visible");
  });


  it("Can view and modify sessions settings", () => {
    cy.dataCy("project-navbar").contains("Settings").should("exist").click();
    cy.intercept("/ui-server/api/renku/*/config.set").as("configSet");
    // ? The settings page refreshes when stale. We should wait for that only when it's invoked.
    let configInvocations = 0;
    cy.intercept("/ui-server/api/renku/*/config.show?git_url=*", req => { configInvocations++; }).as("getConfig");

    const navigateToSettingsSessions = () => {
      const invoked = configInvocations;
      robustNavigateToProjectPage("/settings");
      cy.get(".form-rk-green form").contains("Project Tags").should("exist");
      robustNavigateToProjectPage("/settings/sessions");
      cy.get("h3").contains("Session settings").should("exist");
      if (invoked > configInvocations)
        cy.wait("@configShow", { timeout: TIMEOUTS.long });
    };

    // Make sure the renku.ini is in a pristine state
    robustNavigateToProjectPage("/files");
    cy.get("div#tree-content").contains(".renku").should("exist").click();
    cy.get("div#tree-content").contains("renku.ini").should("exist").click();
    cy.get("pre.hljs").should("be.visible");
    cy.get("pre.hljs").contains("cpu_request").should("not.exist");

    navigateToSettingsSessions();
    cy.get("div.form-rk-green div.row").contains("button", "0.5").should("exist").click();
    cy.wait("@configSet");
    cy.get("div.form-rk-green div.success-feedback").contains("Updated.").should("be.visible");

    robustNavigateToProjectPage("/files");
    cy.get("div#tree-content").contains("renku.ini").should("exist").click();
    cy.get(".hljs.language-ini").contains("[interactive]").should("be.visible");
    cy.get("pre.hljs").contains("cpu_request = 0.5").should("exist");

    navigateToSettingsSessions();
    cy.get("#cpu_request_reset").should("be.visible").click();
    cy.wait("@configSet");
    cy.get("div.form-rk-green div.success-feedback").contains("Updated.").should("exist");

    robustNavigateToProjectPage("/files");
    cy.get("div#tree-content").contains("renku.ini").should("exist").click();
    cy.get(".hljs.language-ini").contains("[interactive]").should("be.visible");
    cy.get("pre.hljs").contains("cpu_request").should("not.exist");
  });
});
