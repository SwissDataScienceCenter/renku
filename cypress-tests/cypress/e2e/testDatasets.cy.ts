import { TIMEOUTS } from "../../config";
import {
  ProjectIdentifier,
  generatorProjectName,
} from "../support/commands/projects";
import { validateLogin, getRandomString } from "../support/commands/general";
import { generatorDatasetName } from "../support/commands/datasets";

const username = Cypress.env("TEST_USERNAME");

const projectTestConfig = {
  shouldCreateProject: true,
  projectName: generatorProjectName("testDatasets"),
};

// ? Uncomment to debug using an existing project
// projectTestConfig.shouldCreateProject = false;
// projectTestConfig.projectName = "cypress-usedatasets-a572ce0e177d";

const projectIdentifier: ProjectIdentifier = {
  name: projectTestConfig.projectName,
  namespace: username,
};
const generatedDatasetName = generatorDatasetName("Dataset");

const sessionId = ["testDatasets", getRandomString()];

describe("Basic datasets functionality", () => {
  before(() => {
    // Use a session to preserve login data
    cy.session(
      sessionId,
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

    // Intercept listing datasets
    cy.intercept("/ui-server/api/renku/*/datasets.list?git_url=*", (req) => {
      listDatasetsInvoked = true;
    }).as("listDatasets");
  });

  beforeEach(() => {
    // Restore the session
    cy.session(
      sessionId,
      () => {
        cy.robustLogin();
      },
      validateLogin
    );
    cy.visitAndLoadProject(projectIdentifier);

    // Reset dataset interceptor
    listDatasetsInvoked = false;
  });

  after(() => {
    if (projectTestConfig.shouldCreateProject)
      cy.deleteProjectFromAPI(projectIdentifier);
  });

  const keywords = ["test", "automated test", "Cypress test"];
  const description = "This is a test dataset form a Cypress tests";

  let listDatasetsInvoked = false;

  it("Create a dataset", () => {
    cy.getProjectSection("Datasets").click();
    if (listDatasetsInvoked)
      cy.wait("@listDatasets", { timeout: TIMEOUTS.long });

    // A new project should not contain datasets
    if (projectTestConfig.shouldCreateProject) {
      cy.contains("No datasets found for this project.", {
        timeout: TIMEOUTS.vlong,
      }).should("be.visible");
    }
    // Create a dataset
    cy.get("#plus-dropdown").should("exist").click();
    cy.get("#navbar-dataset-new").should("exist").click();
    cy.getDataCy("input-title").type(generatedDatasetName.name);

    cy.getDataCy("input-keywords").type(
      keywords.reduce((text, value) => `${text}${value}{enter}`, "")
    );
    cy.get("div.ck.ck-editor__main div.ck.ck-content")
      .should("exist")
      .type(description);
    listDatasetsInvoked = false;
    cy.getDataCy("submit-button").click();
    cy.get(".progress-box").should("be.visible");
    if (listDatasetsInvoked)
      cy.wait("@listDatasets", { timeout: TIMEOUTS.long });

    // Check that the content is correct
    cy.getDataCy("dataset-title")
      .contains(generatedDatasetName.name)
      .should("be.visible");
    for (const keyword of keywords) {
      cy.getDataCy("entity-tag-list")
        .contains(`#${keyword}`)
        .should("be.visible");
    }
    cy.contains(description).get(".renku-markdown").should("be.visible");
  });

  it("Modify the dataset and search for it", () => {
    // Add a keyword and check it is visible
    cy.getProjectSection("Datasets").click();
    if (listDatasetsInvoked)
      cy.wait("@listDatasets", { timeout: TIMEOUTS.long });
    cy.getDataCy("list-card-title")
      .contains(generatedDatasetName.name)
      .should("be.visible")
      .click();
    cy.getDataCy("dataset-file-title")
      .contains("Dataset files")
      .should("be.visible");
    cy.getDataCy("edit-dataset-button").last().click();
    const newKeyword = "additioanl keyword";
    cy.getDataCy("input-keywords").type(`${newKeyword}{enter}`);
    listDatasetsInvoked = false;
    cy.getDataCy("submit-button").click();
    cy.contains("Modifying dataset").should("be.visible");
    if (listDatasetsInvoked)
      cy.wait("@listDatasets", { timeout: TIMEOUTS.long });
    keywords.push(newKeyword);
    for (const keyword of keywords) {
      cy.getDataCy("entity-tag-list")
        .contains(`#${keyword}`)
        .should("be.visible");
    }

    // Search for the dataset after the project has been indexed
    cy.getDataCy("go-back-button").should("be.visible").click();
    cy.waitMetadataIndexing();
    cy.searchForDataset(generatedDatasetName.name);
  });

  it("Delete the dataset", () => {
    cy.getProjectSection("Datasets").click();
    if (listDatasetsInvoked)
      cy.wait("@listDatasets", { timeout: TIMEOUTS.long });
    cy.getDataCy("list-card-title")
      .contains(generatedDatasetName.name)
      .should("be.visible")
      .click();

    cy.getDataCy("delete-dataset-button").click();
    cy.contains("Are you sure you want to delete dataset").should("be.visible");
    cy.get(".modal").contains("Delete dataset").click();
    cy.get(".modal").contains("Deleting dataset...").should("be.visible");

    // Check the dataset is gone after the project has been indexed
    cy.waitMetadataIndexing();
    // ! Currently, datasets don't disappear instantly because the UI uses a renku-core API
    // ! We can leave this disabled until that's addressed
    // cy.searchForDataset(generatedDatasetName.slug, false);
  });
});
