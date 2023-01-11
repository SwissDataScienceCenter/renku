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
// projectTestConfig.projectName = "test-project-273a3030-7967-4467-8358-85d43c990201";

const projectIdentifier = { name: projectTestConfig.projectName, namespace: username };


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
    // load project and wait for the relevant resources to be loaded
    cy.intercept("/ui-server/api/user").as("getUser");
    cy.intercept("/ui-server/api/renku/*/datasets.list*").as("loadDatasets");
    cy.visitProject(projectIdentifier);
    cy.wait("@getUser");
    cy.wait("@loadDatasets", { timeout: TIMEOUTS.long }); // ? Well know slowest API. Wait for all instead?
    cy.wait(1000); // ? This is bad practice, we should look for "something" to wait for
  });

  it("Can search for project", () => {
    // assess the project has been indexed properly -- this might take time if it was recently created
    cy.dataCy("project-overview-nav").contains("Status").should("be.visible").click();
    cy.dataCy("project-overview-content").contains("Knowledge Graph integration is active")
      .should("be.visible", { timeout: TIMEOUTS.long });
    cy.searchForProject(projectIdentifier);

    // logout and search for the project and log back in
    cy.logout();
    cy.get("#nav-hamburger").click();
    cy.searchForProject(projectIdentifier);
    cy.robustLogin();
  });

  it("Can can see overview content", () => {
    cy.contains("README.md").should("be.visible");
    cy.contains("This is a Renku project").should("be.visible");
    cy.contains("Welcome to your new Renku project", { timeout: TIMEOUTS.vlong }).should("be.visible");
    cy.contains("Status").click();
    cy.contains("This project is using the latest version of renku").should("be.visible");
    cy.contains("This project is using the latest version of the template").should("be.visible");
    cy.contains("Knowledge Graph integration is active").should("be.visible");
  });

  it("Can can view files", () => {
    cy.contains("Files").click();
    cy.get("div#tree-content").contains(".renku").click();
    cy.get("div#tree-content").contains("metadata").click();
    cy.getProjectPageLink(projectIdentifier, "/files/blob/.renku/metadata/project").click();
    cy.contains("\"@renku_data_type\": \"renku.domain_model.project.Project\"").should("be.visible");
    cy.get("div#tree-content").contains("README.md").click();
    cy.contains("This is a Renku project").should("be.visible");
  });

  it("Can can work with datasets", () => {
    let migrationInvokedTimes = 0;
    cy.intercept("/ui-server/api/renku/cache.migrations_check*", req => {
      migrationInvokedTimes++;
    }).as("checkMigrations");
    const datasetName = `test-dataset-${uuidv4()}`;
    const datasetTitle = datasetName.replace("-", " ");
    const migrations = migrationInvokedTimes;
    cy.getProjectPageLink(projectIdentifier, "/datasets").click();
    // Wait for migrations when necessary -- this helps when manually debugging issues
    if (migrationInvokedTimes > migrations)
      cy.wait("@checkMigrations");
    // A project we just created should have no datasets
    if (projectTestConfig.shouldCreateProject)
      cy.contains("No datasets found for this project.", { timeout: TIMEOUTS.vlong }).should("be.visible");

    // Create a dataset
    cy.get("a#plus-dropdown").click();
    cy.getProjectPageLink(projectIdentifier, "/datasets/new").last().click();
    cy.dataCy("input-title").type(datasetTitle);
    cy.dataCy("input-keywords").type("test{enter}automated test{enter}");
    cy.get(".ck.ck-content").type("This is a test dataset");
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
    cy.get(".renku-markdown").contains("This is a test dataset").should("exist");
    cy.contains(datasetTitle).should("be.visible").click();

    // Delete the dataset
    cy.dataCy("delete-dataset-button").click();
    cy.contains("Are you sure you want to delete dataset").should("be.visible");
    cy.get(".modal").contains("Delete dataset").click();
  });

  it("Can view and modify settings", () => {
    cy.dataCy("project-navbar").contains("Settings").click();
    const projectUrl = projectUrlFromIdentifier(projectIdentifier);
    // The clone url does not include "/projects" in it
    const cloneSubUrl = projectUrl.substring("/projects".length);
    cy.contains("renku clone").should("contain.text", cloneSubUrl);

    function robustNavigateToProjectPage(page: string) {
      cy.getProjectPageLink(projectIdentifier, page).first().then(($el) => {
        // This page is unstable and we sometimes need to requery
        Cypress.dom.isDetached($el) ?
          cy.getProjectPageLink(projectIdentifier, page) :
          $el;
      }).first().click();
    }

    cy.intercept("/ui-server/api/renku/*/config.set").as("configSet");

    // The settings page refreshed when stale, causing DOM elements to be invalidated.
    // For that reason we wait for the API only when it was invoked
    let configInvokedTimes = 0;
    cy.intercept("/ui-server/api/renku/*/config.show?git_url=*", req => {
      configInvokedTimes++;
    }).as("configShow");
    const navigateToSettingsSessions = () => {
      const invoked = configInvokedTimes;
      robustNavigateToProjectPage("/settings");
      robustNavigateToProjectPage("/settings/sessions");
      if (invoked > configInvokedTimes)
        cy.wait("@configShow", { timeout: TIMEOUTS.standard });
    };

    // Make sure the renku.ini is in a pristine state
    robustNavigateToProjectPage("/files");
    cy.get("div#tree-content").contains(".renku").click();
    cy.get("div#tree-content").contains("renku.ini").click();
    cy.get("pre.hljs").should("be.visible");
    cy.get("pre.hljs").contains("cpu_request").should("not.exist");

    navigateToSettingsSessions();
    cy.get("div.form-rk-green div.row").contains("button", "0.5").click();
    cy.wait("@configSet");
    cy.get("div.form-rk-green div.success-feedback").contains("Updated.").should("exist");

    robustNavigateToProjectPage("/files");
    cy.get("div#tree-content").contains("renku.ini").click();
    cy.get(".hljs.language-ini").contains("[interactive]").should("be.visible");
    cy.get("pre.hljs").contains("cpu_request = 0.5").should("exist");

    navigateToSettingsSessions();
    cy.get("#cpu_request_reset").should("be.visible").click();
    cy.wait("@configSet");
    cy.get("div.form-rk-green div.success-feedback").contains("Updated.").should("exist");

    robustNavigateToProjectPage("/files");
    cy.get("div#tree-content").contains("renku.ini").click();
    cy.get(".hljs.language-ini").contains("[interactive]").should("be.visible");
    cy.get("pre.hljs").contains("cpu_request").should("not.exist");
  });
});
