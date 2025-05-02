import { TIMEOUTS } from "../../config";
import {
  ProjectIdentifier,
  generatorProjectName,
} from "../support/commands/projects";
import { validateLogin, getRandomString } from "../support/commands/general";
import { generatorDatasetName } from "../support/commands/datasets";

const username = Cypress.env("TEST_USERNAME");

const projectTestConfig = {
  projectAlreadyExists: false,
  projectName: generatorProjectName("testDatasets"),
};

// ? Uncomment to debug using an existing project
// projectTestConfig.projectAlreadyExists = true;
// projectTestConfig.projectName = "cypress-usedatasets-a572ce0e177d";

const projectIdentifier: ProjectIdentifier = {
  name: projectTestConfig.projectName,
  namespace: username,
};

const sessionId = ["testDatasets", getRandomString()];

describe("Basic datasets functionality", () => {
  beforeEach(() => {
    // Restore the session
    cy.session(
      sessionId,
      () => {
        cy.robustLogin();
      },
      validateLogin,
    );
    cy.createProjectIfMissing({ templateName: "Python", ...projectIdentifier });
    cy.visitAndLoadProject(projectIdentifier);
  });

  after(() => {
    if (!projectTestConfig.projectAlreadyExists)
      cy.deleteProjectFromAPI(projectIdentifier);
  });

  const keywords = ["test", "automated test", "Cypress test"];
  const description = "This is a test dataset form a Cypress tests";

  it("A new project should have no datasets", () => {
    const emptyProject: ProjectIdentifier = {
      name: generatorProjectName("testDatasets"),
      namespace: username,
    };
    cy.createProjectIfMissing({ templateName: "Python", ...emptyProject });
    cy.visitAndLoadProject(emptyProject);
    cy.getProjectSection("Datasets").click();
    cy.wait("@getDatasets", { timeout: TIMEOUTS.long });
    cy.contains("No datasets found for this project.", {
      timeout: TIMEOUTS.vlong,
    }).should("be.visible");
  });

  it("Create a dataset", () => {
    const generatedDatasetName = generatorDatasetName("Dataset");

    // Reload page to clear the client-side cache
    cy.reload();
    cy.getProjectSection("Datasets").click();
    cy.wait("@getDatasets", { timeout: TIMEOUTS.long });

    // Create a dataset
    cy.get("#plus-dropdown").should("exist").click();
    cy.get("#navbar-dataset-new").should("exist").click();
    cy.getDataCy("input-title").type(generatedDatasetName.name);

    cy.getDataCy("input-keywords").type(
      keywords.reduce((text, value) => `${text}${value}{enter}`, ""),
    );
    cy.get("div.ck.ck-editor__main div.ck.ck-content")
      .should("exist")
      .type(description);
    cy.intercept("POST", "/ui-server/api/renku/*/datasets.create").as(
      "createDataset",
    );
    cy.getDataCy("submit-button").click();
    cy.get(".progress-box").should("be.visible");

    cy.wait("@createDataset", { timeout: TIMEOUTS.long });
    cy.wait("@getDatasets", { timeout: TIMEOUTS.long });

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
    const generatedDatasetName = generatorDatasetName("Dataset");

    // Reload page to clear the client-side cache
    cy.reload();
    cy.getProjectSection("Datasets").click();
    cy.wait("@getDatasets", { timeout: TIMEOUTS.long });

    // Create a dataset
    cy.get("#plus-dropdown").should("exist").click();
    cy.get("#navbar-dataset-new").should("exist").click();
    cy.getDataCy("input-title").type(generatedDatasetName.name);

    cy.getDataCy("input-keywords").type(
      keywords.reduce((text, value) => `${text}${value}{enter}`, ""),
    );
    cy.get("div.ck.ck-editor__main div.ck.ck-content")
      .should("exist")
      .type(description);
    cy.intercept("POST", "/ui-server/api/renku/*/datasets.create").as(
      "createDataset",
    );
    cy.getDataCy("submit-button").click();
    cy.get(".progress-box").should("be.visible");

    cy.wait("@createDataset", { timeout: TIMEOUTS.long });
    cy.wait("@getDatasets", { timeout: TIMEOUTS.long });

    // Add a keyword and check it is visible
    cy.visitAndLoadProject(projectIdentifier);
    cy.getProjectSection("Datasets").click();
    cy.wait("@getDatasets", { timeout: TIMEOUTS.long });
    cy.getDataCy("list-card-title")
      .contains(generatedDatasetName.name)
      .should("be.visible")
      .click();
    cy.getDataCy("dataset-file-title")
      .contains("Dataset files")
      .should("be.visible");
    cy.getDataCy("edit-dataset-button").last().click();
    const newKeyword = "additional keyword";
    cy.getDataCy("input-keywords").type(`${newKeyword}{enter}`);
    cy.intercept("POST", "/ui-server/api/renku/*/datasets.edit").as(
      "modifyDataset",
    );
    cy.getDataCy("submit-button").click();
    cy.contains("Modifying dataset").should("be.visible");
    cy.wait("@modifyDataset", { timeout: TIMEOUTS.long });
    cy.wait("@getDatasets", { timeout: TIMEOUTS.long });
    const updatedKeywords = [...keywords, newKeyword];
    for (const keyword of updatedKeywords) {
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
    const generatedDatasetName = generatorDatasetName("Dataset");

    // Reload page to clear the client-side cache
    cy.reload();
    cy.getProjectSection("Datasets").click();
    cy.wait("@getDatasets", { timeout: TIMEOUTS.long });

    // Create a dataset
    cy.get("#plus-dropdown").should("exist").click();
    cy.get("#navbar-dataset-new").should("exist").click();
    cy.getDataCy("input-title").type(generatedDatasetName.name);

    cy.getDataCy("input-keywords").type(
      keywords.reduce((text, value) => `${text}${value}{enter}`, ""),
    );
    cy.get("div.ck.ck-editor__main div.ck.ck-content")
      .should("exist")
      .type(description);
    cy.intercept("POST", "/ui-server/api/renku/*/datasets.create").as(
      "createDataset",
    );
    cy.getDataCy("submit-button").click();
    cy.get(".progress-box").should("be.visible");

    cy.wait("@createDataset", { timeout: TIMEOUTS.long });
    cy.wait("@getDatasets", { timeout: TIMEOUTS.long });

    // cy.getDataCy("list-card-title")
    //   .contains(generatedDatasetName.name)
    //   .should("be.visible")
    //   .click();

    // Delete the dataset
    cy.getDataCy("delete-dataset-button").click();
    cy.contains("Are you sure you want to delete dataset").should("be.visible");
    cy.get(".modal").contains("Delete dataset").click();
    cy.get(".modal").contains("Deleting dataset...").should("be.visible");

    // Check the dataset is gone after the project has been indexed
    // ! Currently, datasets don't disappear instantly because the UI uses a renku-core API
    // ! We can leave this disabled until that's addressed
    // cy.waitMetadataIndexing();
    // cy.searchForDataset(generatedDatasetName.slug, false);
  });
});
