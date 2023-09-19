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
// projectTestConfig.projectName = "cypress-publicproject-8e01a2e0a8c1";

const projectIdentifier: ProjectIdentifier = {
  name: projectTestConfig.projectName,
  namespace: username,
};

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

  it("Can search for project", () => {
    // Assess the project has been indexed properly.This might take time for new projects.
    cy.getProjectSection("Settings").click();
    cy.getDataCy("project-settings-knowledge-graph")
      .contains("Project indexing", { timeout: TIMEOUTS.vlong })
      .should("exist");
    cy.getDataCy("kg-status-section-open").should("exist").click();
    cy.getDataCy("project-settings-knowledge-graph")
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
    cy.getProjectSection("Overview").click();
    cy.contains("README.md").should("be.visible");
    cy.contains("This is a Renku project").should("be.visible");
    if (projectTestConfig.shouldCreateProject) {
      cy.contains("Welcome to your new Renku project", {
        timeout: TIMEOUTS.vlong,
      }).should("be.visible");
    }

    // Check the URL to clone is correct.
    const projectUrl = projectUrlFromIdentifier(projectIdentifier);
    // ? The clone url does not include "/projects" in it
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
    cy.getDataCy("project-status-icon-element").should("not.exist");
    cy.getProjectSection("Settings").click();
    cy.getDataCy("project-version-section-open").should("exist").click();
    if (projectTestConfig.shouldCreateProject) {
      cy.getDataCy("project-settings-migration-status")
        .contains("This project uses the latest")
        .should("exist");
    }
    cy.getDataCy("project-settings-knowledge-graph")
      .contains("Project indexing")
      .should("exist");
  });

  it("Can view files", () => {
    cy.getProjectSection("Files").click();
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

  it("Can view and modify sessions settings", () => {
    cy.intercept("/ui-server/api/renku/*/config.set").as("configSet");
    cy.intercept("/ui-server/api/renku/*/config.show?git_url=*").as(
      "getConfig"
    );

    // Make sure the renku.ini is in a pristine state
    cy.getProjectSection("Files").click();
    cy.get("div#tree-content").contains(".renku").should("exist").click();
    cy.get("div#tree-content").contains("renku.ini").should("exist").click();
    cy.get("pre.hljs").should("be.visible");
    cy.get("pre.hljs").contains("cpu_request").should("not.exist");

    // Add a compute requirement for sessions
    cy.getProjectSection("Settings").click();
    cy.getDataCy("settings-navbar")
      .contains("a", "Sessions")
      .should("exist")
      .click();
    cy.wait("@getConfig", { timeout: TIMEOUTS.long });
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

    cy.getProjectSection("Files").click();
    cy.get("#tree-content").contains("renku.ini").should("exist").click();
    cy.get(".hljs.language-ini").contains("[interactive]").should("be.visible");
    cy.get("pre.hljs").contains("cpu_request = 1.5").should("exist");

    cy.getProjectSection("Settings").click();
    cy.getDataCy("settings-navbar")
      .contains("a", "Sessions")
      .should("exist")
      .click();
    cy.get("#project-settings-sessions-interactive-cpu-request-reset")
      .should("be.visible")
      .click();
    cy.contains(".badge", "Saving");
    cy.wait("@configSet");
    cy.contains(".badge", "Saved");

    cy.getProjectSection("Files").click();
    cy.get("div#tree-content").contains("renku.ini").should("exist").click();
    cy.get(".hljs.language-ini").contains("[interactive]").should("be.visible");
    cy.get("pre.hljs").contains("cpu_request").should("not.exist");
  });

  it("Can delete the project from the UI", () => {
    cy.deleteProject(projectIdentifier, true);
    cy.searchForProject(projectIdentifier, false);
  });
});
