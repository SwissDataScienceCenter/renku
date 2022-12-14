import { v4 as uuidv4 } from "uuid";
import { TIMEOUTS } from "../../config";

import { projectUrlFromIdentifier } from "../support/commands/projects";

const username = Cypress.env("TEST_USERNAME");

const projectTestConfig = {
  shouldCreateProject: true,
  projectName: `test-project-${uuidv4()}`
};

// Modify the config -- useful for debugging
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
    cy.visitProject(projectIdentifier);
  });

  it("Can search for project", () => {
    cy.visit("/");
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
    //cy.contains("Knowledge Graph integration is active").should("be.visible");
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
    const datasetName = `test-dataset-${uuidv4()}`;
    const datasetTitle = datasetName.replace("-", " ");
    cy.getProjectPageLink(projectIdentifier, "/datasets").click();
    if (projectTestConfig.shouldCreateProject)
    // A project we just created should have no datasets
      cy.contains("No datasets found for this project.", { timeout: TIMEOUTS.vlong }).should("be.visible");

    // Create a dataset
    cy.get("a#plus-dropdown").click();
    cy.getProjectPageLink(projectIdentifier, "/datasets/new").last().click();
    cy.dataCy("input-title").type(datasetTitle);
    cy.dataCy("input-keywords").type("test{enter}automated test{enter}");
    cy.get(".ck.ck-content").type("This is a test dataset");
    cy.intercept("/ui-server/api/renku/9/datasets.list?git_url=*").as("listDatasets");
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

    // // Check that we can see the dataset
    cy.getProjectPageLink(projectIdentifier, "/datasets").click();
    cy.contains(datasetTitle).should("be.visible").click();

    // // Delete the dataset
    cy.dataCy("delete-dataset-button").click();
    cy.contains("Are you sure you want to delete dataset").should("be.visible");
    cy.get(".modal").contains("Delete dataset").click();
  });

  it("Can view and modify settings", () => {

    cy.visitProjectPageLink(projectIdentifier, "/settings");
    const projectUrl = projectUrlFromIdentifier(projectIdentifier);
    // The clone url does not inclue "/projects" in it
    const cloneSubUrl = projectUrl.substring("/projects".length);
    cy.contains("renku clone").should("contain.text", cloneSubUrl);

    // TODO: Check that session settings work

    // function robustNavigateToProjectPage(page: string) {
    //   cy.getProjectPageLink(projectIdentifier, page).first().then(($el) => {
    //     // This page is unstable and we sometimes need to requery
    //     Cypress.dom.isDetached($el) ?
    //       cy.getProjectPageLink(projectIdentifier, page) :
    //       $el;
    //   }).first().click();
    // }

    // function robustGet(selector: string) {
    //   return cy.get(selector).first().then(($el) => {
    //     // This page is unstable and we sometimes need to requery
    //     Cypress.dom.isDetached($el) ?
    //       cy.get(selector) :
    //       $el;
    //   });
    // }

    // The settings page is reflowed a lot, causing DOM elements to be invalidated.
    // For that reason we visit the settings page rather than navigating to it.
    // const navigateToSettingsSessions = () => {
    //   cy.intercept("/ui-server/api/renku/*/config.show?git_url=*").as("configShow");
    //   cy.visitProjectPageLink(projectIdentifier, "/settings/sessions");
    //   cy.wait("@configShow", { timeout: TIMEOUTS.long });
    // };

    // Make sure the renku.ini is in a pristine state
    // robustNavigateToProjectPage("/files");
    // cy.get("div#tree-content").contains(".renku").click();
    // cy.get("div#tree-content").contains("renku.ini").click();
    // cy.get("code").should("be.visible");
    // cy.get("code").contains("cpu_request = 1").should("not.exist");

    // navigateToSettingsSessions();
    // cy.intercept("/ui-server/api/renku/*/config.set").as("configSet");
    // robustGet("div.form-rk-green").contains("button", "1").click();
    // cy.wait("@configSet");
    // cy.visitProjectPageLink(projectIdentifier, "/files/blob/.renku/renku.ini");
    // robustGet("code").contains("cpu_request = 1").should("exist");

    // cy.visitProjectPageLink(projectIdentifier, "/settings/sessions");
    // robustGet("#cpu_request_reset").should("be.visible").click();
  });
});
