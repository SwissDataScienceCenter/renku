import { v4 as uuidv4 } from "uuid";

import { TIMEOUTS } from "../../config";
import {
  ProjectIdentifier,
  generatorProjectName,
  projectUrlFromIdentifier,
} from "../support/commands/projects";
import { validateLogin } from "../support/commands/general";

const username = Cypress.env("TEST_USERNAME");

const projectTestConfig = {
  shouldCreateProject: true,
  projectName: generatorProjectName("publicProject"),
};

// ? Uncomment to debug using an existing project
// projectTestConfig.shouldCreateProject = false;
// projectTestConfig.projectName = "cypress-publicproject-4ed4fb12c5e6";

const projectIdentifier: ProjectIdentifier = {
  name: projectTestConfig.projectName,
  namespace: username,
};

/**
 * Helper function to re-try clicking on a project page link when it detatches from the DOM
 * @param page - target project sub-page
 */
function robustNavigateToProjectPage(page: string) {
  // Requery when the element is detatched
  cy.getProjectPageLink(projectIdentifier, page)
    .first()
    .then(($el) =>
      Cypress.dom.isDetached($el)
        ? cy.getProjectPageLink(projectIdentifier, page)
        : $el
    )
    .first()
    .click();
}

describe("Basic public project functionality", () => {
  before(() => {
    // Use a session to preserve login data
    cy.session(
      "login-publicProject",
      () => {
        cy.robustLogin();
      },
      validateLogin
    );

    // Create a project for the local spec
    if (projectTestConfig.shouldCreateProject) {
      cy.visit("/");
      cy.createProject({ templateName: "Python", ...projectIdentifier });
    }
  });

  beforeEach(() => {
    // Restore the session
    cy.session(
      "login-publicProject",
      () => {
        cy.robustLogin();
      },
      validateLogin
    );
    cy.visitAndLoadProject(projectIdentifier);
  });

  it.only("Can search for project", () => {
    // assess the project has been indexed properly -- this might take time if it was recently created
    cy.dataCy("project-navbar", true)
      .contains("a.nav-link", "Settings")
      .should("be.visible")
      .click();
    
    cy.dataCy("project-settings-knowledge-graph")
      .contains("Project indexing", { timeout: TIMEOUTS.vlong })
      .should("exist");
    cy.dataCy("kg-status-section-open").should("exist").click();
    cy.dataCy("project-settings-knowledge-graph")
      .contains("Everything indexed", { timeout: TIMEOUTS.vlong })
      .should("exist");
    cy.searchForProject(projectIdentifier);

    // logout and search for the project and log back in
    cy.logout();
    cy.get("#nav-hamburger").should("be.visible").click();
    cy.searchForProject(projectIdentifier);
    cy.robustLogin();
  });

  it("Can see overview content and check the clone URLs", () => {
    cy.contains("README.md").should("be.visible");
    cy.contains("This is a Renku project").should("be.visible");
    if (projectTestConfig.shouldCreateProject) {
      cy.contains("Welcome to your new Renku project", {
        timeout: TIMEOUTS.vlong,
      }).should("be.visible");
    }

    // Check the clone URLs
    const projectUrl = projectUrlFromIdentifier(projectIdentifier);
    // The clone url does not include "/projects" in it
    const cloneSubUrl = projectUrl.substring("/projects".length).toLowerCase();
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
  });

  it("Verify project version is up to date", () => {
    cy.contains("Status").should("not.exist");
    cy.dataCy("project-status-icon-element").should("not.exist");
    cy.dataCy("project-navbar", true)
      .contains("a.nav-link", "Settings")
      .should("exist")
      .click();
    cy.dataCy("project-version-section-open").should("exist").click();
    cy.dataCy("project-settings-migration-status")
      .contains("This project uses the latest")
      .should("exist");
    cy.dataCy("project-settings-knowledge-graph")
      .contains("Project indexing")
      .should("exist");
  });

  it("Can view files", () => {
    cy.contains("Files").should("exist").click();
    cy.get("div#tree-content").contains(".renku").should("exist").click();
    cy.get("div#tree-content").contains("metadata").should("exist").click();
    cy.getProjectPageLink(
      projectIdentifier,
      "/files/blob/.renku/metadata/project"
    ).click();
    cy.contains(
      '"@renku_data_type": "renku.domain_model.project.Project"'
    ).should("exist");
    cy.get("div#tree-content").contains("README.md").should("exist").click();
    cy.contains("This is a Renku project")
      .scrollIntoView()
      .should("be.visible");
  });

  it("Can work with datasets", () => {
    let migrationsInvoked = false;
    cy.intercept("/ui-server/api/renku/cache.migrations_check*", (req) => {
      migrationsInvoked = true;
    }).as("checkMigrations");
    const datasetName = `cypress-dataset-${uuidv4().substring(24)}`;
    const datasetTitle = datasetName.replace("-", " ");
    cy.getProjectPageLink(projectIdentifier, "/datasets").click();
    // wait for migrations check to terminate
    if (migrationsInvoked) cy.wait("@checkMigrations");
    // A project we just created should have no datasets
    if (projectTestConfig.shouldCreateProject) {
      cy.contains("No datasets found for this project.", {
        timeout: TIMEOUTS.vlong,
      }).should("be.visible");
    }

    // Create a dataset
    cy.get("#plus-dropdown").should("exist").click();
    cy.get("#navbar-dataset-new").should("exist").click();
    cy.dataCy("input-title").type(datasetTitle);
    cy.dataCy("input-keywords").type("test{enter}automated test{enter}");
    cy.get("div.ck.ck-editor__main div.ck.ck-content")
      .should("exist")
      .type("This is a test dataset");
    cy.intercept("/ui-server/api/renku/*/datasets.list?git_url=*").as(
      "listDatasets"
    );
    cy.dataCy("submit-button").click();
    cy.get(".progress-box").should("be.visible");
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
    cy.contains("Modifying dataset").should("be.visible");
    cy.wait("@listDatasets", { timeout: TIMEOUTS.vlong });
    cy.contains("#modified").should("be.visible");

    // Check that we can see the dataset
    cy.getProjectPageLink(projectIdentifier, "/datasets").click();
    cy.dataCy("entity-description")
      .contains("This is a test dataset")
      .should("exist");
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
    cy.intercept("/ui-server/api/renku/*/config.show?git_url=*").as(
      "getConfig"
    );

    const navigateToSettingsSessions = ({
      waitForApis,
    }: { waitForApis?: boolean } = {}) => {
      robustNavigateToProjectPage("/settings");
      cy.get(".form-rk-green form").contains("Project Tags").should("exist");
      robustNavigateToProjectPage("/settings/sessions");
      cy.get("h3").contains("Session settings").should("exist");
      cy.intercept("/ui-server/api/data/resource_pools").as("getResourcePools");
      if (waitForApis) cy.wait("@getConfig", { timeout: TIMEOUTS.long });
    };

    // Make sure the renku.ini is in a pristine state
    robustNavigateToProjectPage("/files");
    cy.get("div#tree-content").contains(".renku").should("exist").click();
    cy.get("div#tree-content").contains("renku.ini").should("exist").click();
    cy.get("pre.hljs").should("be.visible");
    cy.get("pre.hljs").contains("cpu_request").should("not.exist");

    // Add a compute requirement for sessions
    navigateToSettingsSessions({ waitForApis: true });
    cy.contains("label", "Number of CPUs")
      .parent()
      .find("input.form-control")
      .should("exist")
      .click()
      .type("1.5")
      .blur();
    cy.contains(".badge", "Saving");
    cy.wait("@configSet");
    cy.contains(".badge", "Saved");

    robustNavigateToProjectPage("/files");
    cy.get("div#tree-content").contains("renku.ini").should("exist").click();
    cy.get(".hljs.language-ini").contains("[interactive]").should("be.visible");
    cy.get("pre.hljs").contains("cpu_request = 1.5").should("exist");

    navigateToSettingsSessions();
    cy.get("#project-settings-sessions-interactive-cpu-request-reset")
      .should("be.visible")
      .click();
    cy.contains(".badge", "Saving");
    cy.wait("@configSet");
    cy.contains(".badge", "Saved");

    robustNavigateToProjectPage("/files");
    cy.get("div#tree-content").contains("renku.ini").should("exist").click();
    cy.get(".hljs.language-ini").contains("[interactive]").should("be.visible");
    cy.get("pre.hljs").contains("cpu_request").should("not.exist");
  });

  it("Can delete the project from the UI", () => {
    const slug = projectIdentifier.namespace + "/" + projectIdentifier.name;
    cy.intercept("DELETE", `/ui-server/api/kg/projects/${slug}`).as(
      "deleteProject"
    );

    // Delete the project
    cy.dataCy("project-navbar", true)
      .contains("a.nav-link", "Settings")
      .should("exist")
      .click();
    cy.dataCy("project-settings-general-delete-project")
      .should("be.visible")
      .find("button")
      .contains("Delete project")
      .should("be.visible")
      .click();
    cy.contains("Are you absolutely sure?");
    cy.get("input[name=project-settings-general-delete-confirm-box]").type(
      slug
    );
    cy.get("button")
      .contains("Yes, delete this project")
      .should("be.visible")
      .should("be.enabled")
      .click();
    cy.wait("@deleteProject");

    cy.url().should("not.contain", `/projects/${slug}`);
    cy.get(".Toastify")
      .contains(`Project ${slug} deleted`)
      .should("be.visible");

    // Check that the project is not listed anymore on the search page
    cy.searchForProject(projectIdentifier, false);
  });
});
